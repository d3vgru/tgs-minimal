from jnius import autoclass
from jnius import cast

import AndroidFacade

from TGSSignals import TGSSearchSignal, TGSNewCommunitySignal

from tgscore.discovery.community import DiscoveryCommunity, SearchCache

from tgscore.dispersy.endpoint import StandaloneEndpoint
from tgscore.dispersy.callback import Callback
from tgscore.dispersy.dispersy import Dispersy
from tgscore.dispersy.member import Member
from tgscore.dispersy.dprint import dprint
from tgscore.dispersy.crypto import (ec_generate_key,
        ec_to_public_bin, ec_to_private_bin)

from tgscore.square.community import PreviewCommunity, SquareCommunity


def copySquareToCommunity(square, community):
	# copy fields from Python SquareCommunity to Java TGSCommunity
	# SquareBase properties
	community.setName(square.title)
	community.setDescription(square.description)
	community.setLatitude(square.location[0])
	community.setLongitude(square.location[1])
	community.setRadius(square.radius)
	community.setThumbnailHash(square.thumbnail_hash)
	# Community properties
	community.setCid(square.cid)
        
#TODO: Separate the TGS stuff (dispersy threads setup et al, internal callbacks...) from the pure UI code and put it in this class:
class TGS:
    def __init__(self, workdir):
        AndroidFacade.monitor('TGS: init')
        self._workdir = workdir
        self.callback = None
        self._discovery = None
        self._dispersyInstance = None
        self._my_member = None
        self._TGSCommunity = AndroidFacade.Community()
        self._TGSCommunityList = AndroidFacade.CommunityList()
        #self._TGSListInterface = AndroidFacade.ListInterface()
        #self._TGSListEvent = AndroidFacade.ListEvent()

        AndroidFacade.monitor("TGS: setting up search signals")
        TGSCommunitySearchEvent = AndroidFacade.CommunitySearchEvent()
        self.squareSearchUpdateEvent = TGSCommunitySearchEvent
        self.squareSearchUpdate = TGSSearchSignal(self.squareSearchUpdateEvent)

    #TODO: Add an arg to add the result list widget/model to support multiple search windows.
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
        
    def getSquareForCid(self, cid):
        # FIXME probably not the right way to do this
        if self._dispersyInstance is None:
            return None
        return self._dispersyInstance.get_community(cid)

    ##################################
    #Private methods:
    ##################################
    def _dispersy(self, callback):
        # start Dispersy
        dispersy = Dispersy.get_instance(callback, self._workdir)
        
        # FIXME probably not the right way to do this
        self._dispersyInstance = dispersy
        if AndroidFacade.getConfig().isDispersyEnabled():
            AndroidFacade.monitor('TGS: starting dispersy endpoint')
            dispersy.endpoint = StandaloneEndpoint(dispersy, AndroidFacade.getConfig().getDispersyPort())
            dispersy.endpoint.start()

        # load/join discovery community
        # this is the hardcoded key of the TGS app (aka "App ID")
        # do not change or you'll be a different app :)
        public_key = "3081a7301006072a8648ce3d020106052b81040027038192000406b34f060c416e452fd31fb1770c2f475e928effce751f2f82565bec35c46a97fb8b375cca4ac5dc7d93df1ba594db335350297f003a423e207b53709e6163b7688c0f60a9cf6599037829098d5fbbfe786e0cb95194292f241ff6ae4d27c6414f94de7ed1aa62f0eb6ef70d2f5af97c9aade8266eb85b14296ed2004646838c056d1d9ad8a509b69f81fbc726201b57".decode("HEX")
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
    	
        # load squares (ie master square community)
        # commented out code to send list since dispersy seems to send its own event for each square
        #communityList = self._TGSCommunityList()
        #listEvent = self._TGSListEvent()
        for master in SquareCommunity.get_master_members():
            yield 0.1
            c = dispersy.get_community(master.mid)
            AndroidFacade.monitor('TGS: loaded community: {}'.format(c))
            #community = self._TGSCommunity()
            #copySquareToCommunity(c, community)

            # put in TGSCommunityList
            #communityList.addCommunity(community)
        # send to java
        #superList = cast('org.theglobalsquare.framework.ITGSList', communityList)
        #listEvent.setList(superList)
        #AndroidFacade.monitor('TGS: sending community list event')
        #AndroidFacade.sendEvent(listEvent)

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
        community.set_square_info(title, description, avatar, (int(lat*10**6), int(lon*10**6)), radius)

