import time
import sys
import os

from jnius import autoclass
from jnius import cast

import AndroidFacade

from tgscore.discovery.community import DiscoveryCommunity, SearchCache
from tgscore.square.community import PreviewCommunity, SquareCommunity
from tgscore import events

from tgscore.dispersy.endpoint import StandaloneEndpoint
from tgscore.dispersy.callback import Callback
from tgscore.dispersy.dispersy import Dispersy
from tgscore.dispersy.member import Member
from tgscore.dispersy.dprint import dprint
from tgscore.dispersy.crypto import (ec_generate_key,
        ec_to_public_bin, ec_to_private_bin)


# FIXME use new event model
# dispersy depends on this, so... wtf is it?
#Set up our QT event broker
#events.setEventBrokerFactory(eventproxy.createEventBroker)
#global_events = eventproxy.createEventBroker(None)


# from whirm/tgs-pc tgs_pc/main.py
#CONFIG_FILE_NAME='tgs.conf'


class MainLoop():
    def __init__(self):
        self.go = True
        self._chatCore = None
    def setChatCore(self, chatCore):
        self._chatCore = chatCore
    def run(self):
        """
        # http://stackoverflow.com/questions/1956142/how-to-redirect-stderr-in-python
        r, w = os.pipe()
        os.close(sys.stderr.fileno())
        os.dup2(w, sys.stderr.fileno())
        self.errs = r
        """

        while self.process():
            pass
    def process(self):
        """
        # read 1K from stderr and cc to monitor
        errMsg = os.read(self.errs, 1024)
        if errMsg is not None:
            AndroidFacade.monitor(errMsg)
        """
        #f = AndroidFacade.getMainActivity()
        #AndroidFacade.monitor('MainLoop: TICK, {} events in queue'.format(f.queueSize()))
        nextEvent = AndroidFacade.nextEvent()
        if nextEvent is not None:
            eventClassName = nextEvent.getClass().getName()
            concreteEvent = cast(eventClassName, nextEvent)
            superEvent = cast('org.theglobalsquare.framework.TGSEvent', nextEvent)
#            AndroidFacade.monitor('MainLoop: got event from java, class: {}'.format(eventClass))
            if(eventClassName == 'org.theglobalsquare.framework.values.TGSConfigEvent'):
                # update config using latest values
                # hmm, subject seems to work here
                self._chatCore.setConfig(concreteEvent.getSubject())
            elif(eventClassName == 'org.theglobalsquare.framework.values.TGSCommunitySearchEvent'):
                communityObj = superEvent.getObject()
                if(communityObj is not None):
                    terms = communityObj.getName()
                    AndroidFacade.monitor('ChatCore: got community search terms: {}'.format(terms))
                    # really start search
                    self._chatCore.startNewSquareSearch(terms)
            elif(eventClassName == 'org.theglobalsquare.framework.values.TGSCommunityEvent'):
                # subject works as long as we're the main thread
                subjectObj = cast('org.theglobalsquare.framework.values.TGSCommunity', concreteEvent.getSubject())
                AndroidFacade.monitor('got a community from subject: {}'.format(subjectObj.getName()))
                
                # TODO create the community
                
        time.sleep(.1)
        return self.go


# for simple notifications of a recurring event
class TGSSignal:
    def __init__(self, eventProtoClass):
        self._eventProtoClass = eventProtoClass
        self._objectProtoClass = autoclass('org.theglobalsquare.framework.ITGSObject')
        self._communityProtoClass = autoclass('org.theglobalsquare.framework.values.TGSCommunity')
    def emit(self, *argv, **kwargs):
# argv is something like (<tgscore.discovery.community.SearchCache object at 0x454ad050>, 'finished')
#        AndroidFacade.Event()
        event = self._eventProtoClass()
        # TODO put cache in results
        
        cache = argv[0]
        event.setVerb(argv[1])
        community = self._communityProtoClass()
        AndroidFacade.monitor('Signal: community: {}'.format(community))

        # terms[0] is the (first set of?) terms
        AndroidFacade.monitor('Signal: terms[0]: {}'.format(cache.terms[0]))

        # terms[0][0] is the length of the shortest term
        # terms[0][1] is the name
        # get the name of the first term
        community.setName(cache.terms[0][1])

        # cast or else jnius freaks
        # setting subject doesn't work either? (because it's a callback?)
        # jnius currently freaks anyway if just TGSObject?
        # must include target class as a field (eg _objectProtoClass)?
        # try casting it as whatever it thinks it is
        #concreteObject = cast(community.getClass().getName(), community)
        
        # must cast as the exact type that formal param of setObject() expects
        superObject = cast('org.theglobalsquare.framework.ITGSObject', community)
        event.setObject(superObject)
        AndroidFacade.monitor('Signal: emitting event of type {} with cache {}'.format(self._eventProtoClass, cache))
        AndroidFacade.sendEvent(event)

#TODO: Separate the TGS stuff (dispersy threads setup et al, internal callbacks...) from the pure UI code and put it in this class:
class TGS:
    ##################################
    #Signals:
    ##################################
#    memberSearchUpdate = QtCore.pyqtSignal(SearchCache, 'QString')
#    squareSearchUpdate = QtCore.pyqtSignal(SearchCache, 'QString')
#    textSearchUpdate = QtCore.pyqtSignal(SearchCache, 'QString')
    def __init__(self, workdir):
        AndroidFacade.monitor('TGS: init')
        self._workdir = workdir
        self.callback = None
        self._discovery = None
        self._my_member = None
        
        # get this from main thread or else class not found?
        self._communityListProto = AndroidFacade.CommunityList()
        self._listEventProto = AndroidFacade.ListEvent()

        AndroidFacade.monitor("TGS: setting up search signals")
        TGSCommunitySearchEvent = AndroidFacade.CommunitySearchEvent()
        self.squareSearchUpdateEvent = TGSCommunitySearchEvent
        self.squareSearchUpdate = TGSSignal(self.squareSearchUpdateEvent)
        """
        self.memberSearchUpdateEvent = TGSUserSearchEvent()
        self.memberSearchUpdate = TGSSignal(self.memberSearchUpdateEvent)
        self.squareSearchUpdateEvent = TGSCommunitySearchEvent()
        self.squareSearchUpdate = TGSSignal(self.squareSearchUpdateEvent)
        self.textSearchUpdateEvent = TGSMessageSearchEvent()
        self.textSearchUpdate = TGSSignal(self.textSearchUpdateEvent)
        """

    ##################################
    #Slots:
    ##################################
    #TODO: Add an arg to add the result list widget/model to support multiple search windows.
    # FIXME pass the SearchCache with the event
    def startNewMemberSearch(self, search_terms):
        print "Searching members for:", search_terms
        self._discovery.simple_member_search(search_terms, self.memberSearchUpdate.emit)

    def startNewSquareSearch(self, search_terms):
        print "Searching squares for:", search_terms
        self._discovery.simple_square_search(search_terms, self.squareSearchUpdate.emit)

    def startNewTextSearch(self, search_terms):
        print "Searching text messages for:", search_terms
        self._discovery.simple_text_search(search_terms, self.textSearchUpdate.emit)

    def joinSquare(self, square):
        self.callback.register(square.join_square)

    def leaveSquare(self, square):
        self.callback.register(square.leave_square)

    ##################################
    #Public methods:
    ##################################
    def setupThreads(self):
        # start threads
        callback = Callback()
        if AndroidFacade.getConfig().isDispersyEnabled():
            AndroidFacade.monitor('TGS: starting dispersy callback')
            callback.start(name="Dispersy")
            AndroidFacade.monitor('TGS: registering self._dispersy')
            callback.register(self._dispersy, (callback,))
            if "--simulate" in sys.argv:
                callback.register(self._DEBUG_SIMULATION)
            if "--simulate-qt" in sys.argv:
                callback.register(self._DEBUG_QT_SIMULATION)
        self.callback = callback

    def stopThreads(self):
    	AndroidFacade.monitor('TGS: tearing down threads')
        self.callback.stop()

        if self.callback.exception:
            global exit_exception
            exit_exception = self.callback.exception

    def createNewSquare(self, square_info):
    	AndroidFacade.monitor('TGS: creating new square')
        self.callback.register(self._dispersyCreateCommunity, square_info)

    def sendText(self, community, message, media_hash=''):
    	AndroidFacade.monitor('TGS: sending text')
        self.callback.register(community.post_text, (message, media_hash))

    def setMemberInfo(self, community, alias, thumbnail_hash=''):
    	AndroidFacade.monitor('TGS: setting member info')
        self.callback.register(community.set_my_member_info, (alias,thumbnail_hash))

    ##################################
    #Private methods:
    ##################################
    def _dispersy(self, callback):
        # start Dispersy
        
        dispersy = Dispersy.get_instance(callback, self._workdir)
        if AndroidFacade.getConfig().isDispersyEnabled():
            AndroidFacade.monitor('TGS: starting dispersy endpoint')
            dispersy.endpoint = StandaloneEndpoint(dispersy, AndroidFacade.getConfig().getDispersyPort())
            dispersy.endpoint.start()

        # load/join discovery community
	# FIXME
        public_key = "3081a7301006072a8648ce3d020106052b81040027038192000406b34f060c416e452fd31fb1770c2f475e928effce751f2f82565bec35c46a97fb8b375cca4ac5dc7d93df1ba594db335350297f003a423e207b53709e6163b7688c0f60a9cf6599037829098d5fbbfe786e0cb95194292f241ff6ae4d27c6414f94de7ed1aa62f0eb6ef70d2f5af97c9aade8266eb85b14296ed2004646838c056d1d9ad8a509b69f81fbc726201b57".decode("HEX")
        if False:
            # when crypto.py is disabled a public key is slightly
            # different...
            public_key = ";".join(("60", public_key[:60].encode("HEX"), ""))
        master = Member(public_key)
        try:
            self._discovery = DiscoveryCommunity.load_community(master)
        except ValueError:
            ec = ec_generate_key(u"low")
            self._my_member = Member(ec_to_public_bin(ec), ec_to_private_bin(ec))
            self._discovery = DiscoveryCommunity.join_community(master, self._my_member)
        else:
            self._my_member = self._discovery.my_member

        dispersy.define_auto_load(PreviewCommunity, (self._discovery, False))
        dispersy.define_auto_load(SquareCommunity, (self._discovery,))

    	AndroidFacade.monitor('TGS: loading squares')
        # load squares
        # TODO put in TGSCommunityList and send to java
        TGSCommunity = AndroidFacade.Community()
        TGSCommunityList = AndroidFacade.CommunityList()
        TGSListEvent = AndroidFacade.ListEvent
        communityList = TGSCommunityList()
        for master in SquareCommunity.get_master_members():
            yield 0.1
            c = dispersy.get_community(master.mid)
            AndroidFacade.monitor('TGS: got community: {}'.format(c))
            community = TGSCommunity()
            community.setMid(master.mid)
            communityList.addCommunity(community)
        listEvent = TGSListEvent()
        listEvent.setSubject(communityList)
    	AndroidFacade.monitor('TGS: sending community list event')
        AndroidFacade.sendEvent(listEvent)

    	AndroidFacade.monitor('TGS: dispersy startup complete')
        # let android know we're done initializing
        # monitor will say "EVENT: TGSSystemEvent: start"
        # light turns green
        TGSSystemEvent = AndroidFacade.SystemEvent()
        AndroidFacade.sendEvent(TGSSystemEvent.forStart())

    def _dispersy_onSearchResult(self, result):
        print "OnSearchResult", result

    def _dispersyCreateCommunity(self, title, description, avatar, lat, lon, radius):
        community = SquareCommunity.create_community(self._my_member, self._discovery)

        #TODO: Publish the avatar via swift and set the avatar's hash here
        community.set_square_info(title, description, '', (int(lat*10**6), int(lon*10**6)), radius)

    def _DEBUG_SIMULATION(self):
        yield 5.0

        # user clicked the 'create new square' button
        #community = SquareCommunity.create_community(self._my_member, self._discovery)
        #yield 1.0

        # user clicked the 'update my member info' button
        #community.set_my_member_info(u"SIM nickname", "")
        #yield 1.0

        # user clicked the 'update square info' button
        #community.set_square_info(u"SIM title", u"SIM description", "", (0, 0), 0)
        #yield 1.0

        #for index in xrange(5):
        #    # user clicked the 'post message' button
        #    community.post_text(u"SIM message %d" % index, "")
        #    yield 1.0

        def response_func(cache, event, request_timestamp):
            if cache:
                dprint(round(time.time() - request_timestamp, 2), "s ", event, "! received ", len(cache.suggestions), " suggestions; retrieved ", sum(1 if suggestion.hit else 0 for suggestion in cache.suggestions), " hits", force=1)

        yield 3.0

        for index in xrange(999999):
            # user clicked the 'search' button
            dprint("NEW SEARCH", line=1, force=1)
            now = time.time()
            self._discovery.simple_member_search(u"member test %d" % index, response_func, (now,))
            self._discovery.simple_square_search(u"square test %d" % index, response_func, (now,))
            self._discovery.simple_text_search(u"text test %d" % index, response_func, (now,))
            yield 30.0

    def _DEBUG_QT_SIMULATION(self):
        yield 5.0

        for index in xrange(999999):
            # user clicked the 'search' button
            dprint("NEW QT SEARCH", line=1, force=1)
            self.onSearchSquareClicked()
            yield 1
            self._tgs.startNewSearch("member test $d" % index)
            #now = time.time()
            #self._discovery.simple_member_search(u"member test %d" % index, self._tgs.memberSearchUpdate.emit, (now,))
            #self._discovery.simple_square_search(u"square test %d" % index, response_func, (now,))
            #self._discovery.simple_text_search(u"text test %d" % index, response_func, (now,))
            yield 30.0


class ChatCore:
    def __init__(self):
        self.message_references = []
        self._communities = {}
        self._communities_listwidgets = {}
        self._square_search_dialog = None
        self._message_attachment = None
        self._oldAlias = None
        
    def startNewSquareSearch(self, search_terms):
        self._tgs.startNewSquareSearch(search_terms)

    ##################################
    #Slots:
    ##################################
    """ some of these need to propagate events to UI, but Android layer handles all the business
    #TODO: Refactor the 3 search functions to 3 small ones and a generic one as they are basically the same.
    
    WNI
    def onMemberSearchUpdate(self, cache, event):
        #TODO:
        print "Received member search update"
        #TODO: Deal with status changes and notify user when search is done.
        if self._member_search_dialog:
            self._member_search_dialog.clearResultsList()
            for suggestion in cache.suggestions:
                member = suggestion.hit
                if suggestion.state == 'done':
                    self._member_search_dialog.addResult(member.alias, member.thumbnail_hash)
            if event == "finished":
                self._member_search_dialog.onSearchFinished()
        else:
            print "But the search window doesn't exist, dropping it..."

    4---A - send event
    def onSquareSearchUpdate(self, cache, event):
        #TODO:
        print "Received Square search update"
        #TODO: Deal with status changes and notify user when search is done.
        if self._square_search_dialog:
            self._square_search_dialog.clearResultsList()
            for suggestion in cache.suggestions:
                square = suggestion.hit
                if suggestion.state == 'done':
                    self._square_search_dialog.addResult(square)
            if event == "finished":
                self._square_search_dialog.onSearchFinished()
        else:
            print "But the search window doesn't exist, dropping it..."

    WNI
    def onTextSearchUpdate(self, cache, event):
        #TODO:
        print "Received text search update"
        #TODO: Deal with status changes and notify user when search is done.
        if self._message_search_dialog:
            self._message_search_dialog.clearResultsList()
            for suggestion in cache.suggestions:
                text = suggestion.hit
                if suggestion.state == 'done':
                    self._message_search_dialog.addResult(text.text, text.member, text.square)
            if event == "finished":
                self._message_search_dialog.onSearchFinished()
        else:
            print "But the search window doesn't exist, dropping it..."

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

    2---P - check for event
    def onNewCommunityCreated(self, square):
        #TODO: We need to update the square list here.
        print "New square created", square
        #TODO: We should switch to an MVC widget soon, so we can sort, filter, update, etc easily.

        list_item = SquareOverviewListItem(parent=self.mainwin.squares_list, square=square)
        item_index = self.mainwin.squares_list.row(list_item)
        #Create this square's messages list
        list_widget = QtGui.QListWidget()

        #Setup widget properties
        list_widget.setFrameShape(QtGui.QFrame.NoFrame)
        list_widget.setFrameShadow(QtGui.QFrame.Plain)
        list_widget.setHorizontalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        list_widget.setAutoScroll(True)
        list_widget.setAutoScrollMargin(2)
        list_widget.setEditTriggers(QtGui.QAbstractItemView.NoEditTriggers)
        list_widget.setProperty("showDropIndicator", False)
        list_widget.setDragDropMode(QtGui.QAbstractItemView.NoDragDrop)
        list_widget.setSelectionMode(QtGui.QAbstractItemView.NoSelection)
        list_widget.setVerticalScrollMode(QtGui.QAbstractItemView.ScrollPerPixel)
        list_widget.setHorizontalScrollMode(QtGui.QAbstractItemView.ScrollPerPixel)
        list_widget.setMovement(QtGui.QListView.Snap)
        list_widget.setProperty("isWrapping", False)
        list_widget.setSpacing(2)
        list_widget.setWordWrap(True)

        #Scroll to bottom at each new message insertion
        message_model = list_widget.model()
        message_model.rowsInserted.connect(list_widget.scrollToBottom)

        self.mainwin.messages_stack.insertWidget(item_index, list_widget)
        self.mainwin.messages_stack.setCurrentIndex(item_index)

        list_item.setSelected(True)

        self._communities_listwidgets[square.cid]=list_widget

        self._communities[square.cid] = square

        #Set member info for this square
        self._setMemberInfo(square)

        #TODO: Put this on the widget constructor, and remove it from here and onNewPreviewCommunityCreated
        square.events.connect(square.events, QtCore.SIGNAL('squareInfoUpdated'), list_item.onInfoUpdated)
        square.events.connect(square.events, QtCore.SIGNAL('messageReceived'), self.onTextMessageReceived)

    5---AP - check for event
    def onJoinSuggestedCommunity(self):
        #TODO: disable the leave/join buttons if no square is selected
        print "Joining a new community!"
        item = self.mainwin.suggested_squares_list.currentItem()
        if item:
            square = item.square
            self._tgs.joinSquare(square)
        else:
            msg_box = QtGui.QMessageBox()
            msg_box.setText("Please, select which square you want to join from the suggested squares list.")
            msg_box.exec_()

    6---AP - check for event
    def onLeaveCommunity(self):
        print "leaving community!"
        #Get currently selected community
        current_item = self.mainwin.squares_list.currentItem()
        if type(current_item) is SquareOverviewListItem:
            square = current_item.square
            self._tgs.leaveSquare(square)
            row = self.mainwin.squares_list.currentRow()
            self.mainwin.squares_list.takeItem(row)
            #Remove the square reference from the squares list
            self._communities.pop(current_item.square.cid)
        else:
            msg_box = QtGui.QMessageBox()
            msg_box.setText("Please, select which square you want to leave from the top-left list first.")
            msg_box.exec_()

    5---A - send event
    def onNewHotCommunitiesAvailable(self, squares, texts):
        print "New suggestions arrived", squares, texts

        self.mainwin.suggested_squares_list.clear()
        for square in squares:
            list_item = SquareOverviewListItem(parent=self.mainwin.suggested_squares_list, square=square)
            list_item.square = square

    2---A - check for event
    def onCreateSquareBtnPushed(self):
        self.mainwin.createSquare_btn.setEnabled(False)
        self._square_edit_dialog = SquareEditDialog()
        self._square_edit_dialog.squareInfoReady.connect(self.onSquareCreateDialogFinished)
        self._square_edit_dialog.show()
    def onSquareCreateDialogFinished(self):
        square_info = self._square_edit_dialog.getSquareInfo()

        self._tgs.createNewSquare(square_info)

        self._square_edit_dialog = None
        self.mainwin.createSquare_btn.setEnabled(True)

    4---AP - check for event
    def onSearchSquareClicked(self):
        self.mainwin.search_square_btn.setEnabled(False)
        self._square_search_dialog = SquareSearchDialog()
        self._square_search_dialog.rejected.connect(self.onSquareSearchDialogClosed)
        self._square_search_dialog.onSearchRequested.connect(self._tgs.startNewSquareSearch)
        self._square_search_dialog.onJoinSquareRequested.connect(self._tgs.joinSquare)
        self._square_search_dialog.show()
    def onSquareSearchDialogClosed(self):
        self.mainwin.search_square_btn.setEnabled(True)

    WNI
    def onSearchMessageClicked(self):
        self.mainwin.search_message_btn.setEnabled(False)
        self._message_search_dialog = MessageSearchDialog()
        self._message_search_dialog.rejected.connect(self.onMessageSearchDialogClosed)
        self._message_search_dialog.onSearchRequested.connect(self._tgs.startNewTextSearch)
        self._message_search_dialog.show()
    def onMessageSearchDialogClosed(self):
        self.mainwin.search_message_btn.setEnabled(True)
    def onSearchMemberClicked(self):
        self.mainwin.search_member_btn.setEnabled(False)
        self._member_search_dialog = MemberSearchDialog()
        self._member_search_dialog.rejected.connect(self.onMemberSearchDialogClosed)
        self._member_search_dialog.onSearchRequested.connect(self._tgs.startNewMemberSearch)
        self._member_search_dialog.show()
    def onMemberSearchDialogClosed(self):
        self.mainwin.search_member_btn.setEnabled(True)

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
    # not really sure we need this
    def onSquareSearchUpdate(self, cache, event):
        #TODO: send event to Android - 4
        AndroidFacade.monitor('Received Square search update')
        #TODO: Deal with status changes and notify user when search is done.
        """
        if self._square_search_dialog:
            self._square_search_dialog.clearResultsList()
            for suggestion in cache.suggestions:
                square = suggestion.hit
                if suggestion.state == 'done':
                    self._square_search_dialog.addResult(square)
            if event == "finished":
                self._square_search_dialog.onSearchFinished()
        else:
            print "But the search window doesn't exist, dropping it..."
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
        
        # FIXME hook up square search callback

        """ ERK - replace
        self._tgs.squareSearchUpdate.connect(self.onSquareSearchUpdate)
        self._tgs.memberSearchUpdate.connect(self.onMemberSearchUpdate)
        self._tgs.textSearchUpdate.connect(self.onTextSearchUpdate)
        """

        #Setup QT main window
        #self.app = QtGui.QApplication(sys.argv)
        #self.mainwin = MainWin()

        #Set configurable values
        """ FIXME make sure native ui handles this
        self.mainwin.nick_line.setText(self._config['Member']['Alias'])
        thumb_data = QtCore.QBuffer()
        thumb_data.open(thumb_data.ReadWrite)
        thumb_bytes = QtCore.QByteArray.fromBase64(self._config['Member']['Thumbnail'])
        pixmap = QtGui.QPixmap()
        pixmap.loadFromData(thumb_bytes, 'PNG')
        self.mainwin.avatar_btn.setIcon(QtGui.QIcon(pixmap))

        #Connect main window signals
        +1A-->self.mainwin.nick_line.editingFinished.connect(self.onNickChanged)
        self.mainwin.avatar_btn.clicked.connect(self.onThumbnailButtonPressed)
        3A-->self.mainwin.message_line.returnPressed.connect(
                                                self.onMessageReadyToSend)
        3A-->self.mainwin.message_send_btn.clicked.connect(
                                                self.onMessageReadyToSend)
        self.mainwin.attach_btn.toggled.connect(self.onAttachButtonToggled)
        5A-->self.mainwin.join_square_btn.clicked.connect(self.onJoinSuggestedCommunity)
        6A-->self.mainwin.leave_square_btn.clicked.connect(self.onLeaveCommunity)
        2A-->self.mainwin.createSquare_btn.clicked.connect(self.onCreateSquareBtnPushed)

        +4A-->self.mainwin.search_square_btn.clicked.connect(self.onSearchSquareClicked)
        self.mainwin.search_message_btn.clicked.connect(self.onSearchMessageClicked)
        self.mainwin.search_member_btn.clicked.connect(self.onSearchMemberClicked)

        #Hide the tools panel
        self.mainwin.tools_grp.hide()

        #TODO: Refactor this to put it in TGS class
        #Connect global events
        global_events.qt.newHotCommunitiesAvailable.connect(self.onNewHotCommunitiesAvailable)
        global_events.qt.newCommunityCreated.connect(self.onNewCommunityCreated)
        #global_events.qt.newPreviewCommunityCreated.connect(
        #                                        self.onNewPreviewCommunityCreated)
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
        AndroidFacade.monitor('old: {}, new: {}'.format(oldAlias, newAlias))
        if newAlias and (newAlias != oldAlias):
            self._propagateMemberInfoToAll()

    def getConfig(self):
        return self._config
        
    def setConfig(self, config):
        if self._config is not None:
            self._config = config
            self.onNickChanged()
            self._oldAlias = self._config.getName()
#            AndroidFacade.monitor('config refreshed, proxyEnabled: {}'.format(self._config.isProxyEnabled()))


    ##################################
    #Private Methods
    ##################################
    def _getConfig(self):
        # FIXME hardcode config to private file
	    # TODO eventually store actual config on android side
	    # right.
	    # use AndroidFacade to get config stuff
	    # TEST that uninitialized config does what it should
        config_path = '/data/data/org.theglobalsquare.app/files/tgs'

        #Create app data dir if it doesn't exist
        if not os.path.exists(config_path):
            AndroidFacade.monitor('ChatCore: making config dir')
            os.makedirs(config_path)
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


if __name__ == '__main__':
    exit_exception = None
    if exit_exception:
        raise exit_exception
    chat = ChatCore()
    chat.run()
