import os

import AndroidFacade

from MainLoop import MainLoop

from TGS import TGS

class ChatCore:
    def __init__(self):
        self.message_references = []
        self._communities = {}
        self._communities_listwidgets = {}
        self._square_search_dialog = None
        self._message_attachment = None
        self._oldAlias = None
        
    # "public" API
    def startNewSquareSearch(self, search_terms):
        self._tgs.startNewSquareSearch(search_terms)

    def joinSquare(self, square):
        self._tgs.joinSquare(square)

    def leaveSquare(self, square):
        self._tgs.leaveSquare(square)

    def createNewSquare(self, square_info):
        self._tgs.createNewSquare(square_info)

    def getSquareForMid(self, mid):
        return self._tgs.getSquareForMid(mid)
        
    def sendText(self, community, message, media_hash=''):
        self._tgs.sendText(community, message, media_hash)
    
    """ some of these need to propagate events to UI, but Android layer handles all the business

    3---A - send event
    def onTextMessageReceived(self, message):
        #Put the message in the overview list
        ChatMessageListItem(parent=self.mainwin.message_list, message=message)

        while self.mainwin.message_list.count() > 250:
            print "Deleting A chat message"
            self.mainwin.message_list.takeItem(0)

        #Put the message in the square specific list
        square_list_widget = self._communities_listwidgets[message.square.cid]
        ChatMessageListItem(parent=square_list_widget, message=message)

    3---P - check for event
    def onMessageReadyToSend(self):
        message = self.mainwin.message_line.text()
        #TODO: Check if the community where we are sending the message has our member info up to date!!
        if message:
            print "Sending message: ", message
            #Get currently selected community
            current_item = self.mainwin.squares_list.selectedItems()[0]
            if type(current_item) is SquareOverviewListItem:
                square = current_item.square
                #TODO: Add media_hash support, empty string ATM.
                media_hash = ''
                self._tgs.sendText(square, message, media_hash)
                self.mainwin.message_line.clear()
            else:
                msg_box = QtGui.QMessageBox()
                msg_box.setText("Please, select to which square you want to send the message from the the top-left list first.")
                msg_box.exec_()
        else:
            print "I categorically refuse to send an empty message."

    5---A - send event
    def onNewHotCommunitiesAvailable(self, squares, texts):
        print "New suggestions arrived", squares, texts

        self.mainwin.suggested_squares_list.clear()
        for square in squares:
            list_item = SquareOverviewListItem(parent=self.mainwin.suggested_squares_list, square=square)
            list_item.square = square

    7---AP - check for event
    def onThumbnailButtonPressed(self):
        fileName = QtGui.QFileDialog.getOpenFileName(self.mainwin,
                    "Select your avatar", "", "Image Files (*.png *.jpg *.bmp *.gif)"
        )
        image = QtGui.QPixmap(fileName)
        if image.width() > image.height():
            image = image.scaledToWidth(64)
        else:
            image = image.scaledToHeight(64)

        self.mainwin.avatar_btn.setIcon(QtGui.QIcon(image))
        thumb_data = QtCore.QBuffer()
        thumb_data.open(thumb_data.ReadWrite)
        image.save(thumb_data, 'PNG')

        self._config['Member']['Thumbnail'] = thumb_data.buffer().toBase64()
        self._config.write()

    8---A - entirely android
    def onAttachButtonToggled(self, status):
        if status:
            self._message_attachment = QtGui.QFileDialog.getOpenFileName(self.mainwin,
                                                    "Attach file to message", "", "")
            self.mainwin.attach_btn.setToolTip(self._message_attachment)
        else:
            self._message_attachment = None
            self.mainwin.attach_btn.setToolTip('')
    """

    ##################################
    #Public Methods
    ##################################
    def run(self):
        #Read config file
        self._getConfig()

        #Setup TGS core
        AndroidFacade.monitor('ChatCore.run: TGS startup')
        self._tgs = TGS(self._workdir)
        
        """
        #Connect main window signals
        +1A-->self.mainwin.nick_line.editingFinished.connect(self.onNickChanged)
        +2A-->self.mainwin.createSquare_btn.clicked.connect(self.onCreateSquareBtnPushed)
        3A-->self.mainwin.message_line.returnPressed.connect(self.onMessageReadyToSend)
        3A-->self.mainwin.message_send_btn.clicked.connect(self.onMessageReadyToSend)
        +4A-->self.mainwin.search_square_btn.clicked.connect(self.onSearchSquareClicked)
        +5A-->self.mainwin.join_square_btn.clicked.connect(self.onJoinSuggestedCommunity)
        +6A-->self.mainwin.leave_square_btn.clicked.connect(self.onLeaveCommunity)
        7A-->self.mainwin.avatar_btn.clicked.connect(self.onThumbnailButtonPressed)
        8A-->self.mainwin.attach_btn.toggled.connect(self.onAttachButtonToggled)

        #TODO: Refactor this to put it in TGS class
        #Connect global events
        global_events.qt.newHotCommunitiesAvailable.connect(self.onNewHotCommunitiesAvailable)
        #global_events.qt.newPreviewCommunityCreated.connect(self.onNewPreviewCommunityCreated)
        """
        
        """ TODO get global_events working without Qt...
        #global_events.qt.newCommunityCreated.connect(self.onNewCommunityCreated)
        # TODO ...and set this as the callback for when a community is created
		self._communities[square.cid] = square
		#Set member info for this square
		self._setMemberInfo(square)
		square.events.connect(square.events, QtCore.SIGNAL('squareInfoUpdated'), list_item.onInfoUpdated)
		square.events.connect(square.events, QtCore.SIGNAL('messageReceived'), self.onTextMessageReceived)
		"""

        #Setup dispersy threads
        AndroidFacade.monitor('ChatCore.run: setting up threads')
        self._tgs.setupThreads()
        
        AndroidFacade.monitor('Chatcore.run: entering main loop')
        mainLoop = MainLoop()
        mainLoop.setChatCore(self)
        mainLoop.run()

        #Destroy dispersy threads before exiting
        AndroidFacade.monitor('ChatCore.run: destroying threads')
        self._tgs.stopThreads()
        
    # +1---AP - check for event
    def onNickChanged(self, *argv, **kwargs):
        oldAlias = self._oldAlias
        newAlias = self._config.getName()
        AndroidFacade.monitor('oldAlias: {}, newAlias: {}'.format(oldAlias, newAlias))
        if newAlias and (newAlias != oldAlias):
            self._propagateMemberInfoToAll()

    def getConfig(self):
        return self._config
        
    def setConfig(self, config):
        if self._config is not None:
            self._config = config
            self.onNickChanged()
            self._oldAlias = self._config.getName()


    ##################################
    #Private Methods
    ##################################
    def _getConfig(self):
        config_path = os.environ['ANDROID_PRIVATE'] + '/tgs'
        files_path = os.environ['ANDROID_PRIVATE'] + '/private'

        #Create app data dir if it doesn't exist
        if not os.path.exists(config_path):
            AndroidFacade.monitor('ChatCore: making config dir')
            os.makedirs(config_path)
        if not os.path.exists(files_path):
            AndroidFacade.monitor('ChatCore: making files dir')
            os.makedirs(files_path)
        self._workdir = unicode(config_path)
        self._config = AndroidFacade.getConfig()
        self._oldAlias = self._config.getName()

    def _propagateMemberInfoToAll(self):
        #TODO: Check if the community has up to date info before sending unnecessary updates
        AndroidFacade.monitor('ChatCore: propagating member info')
        for community in self._communities.itervalues():
            self._setMemberInfo(community)

    def _setMemberInfo(self, community):
        alias = self._config.getAlias()
        thumbnail = '' #str(self._config['Member']['Thumbnail']) #TODO: Setup this correctly when swift gets integrated
        self._tgs.setMemberInfo(community, alias, thumbnail)

