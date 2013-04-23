import logging
logger = logging.getLogger(__name__)

import sys
from threading import Event

from jnius import autoclass
from jnius import cast

import AndroidFacade

from tgscore.discovery.community import DiscoveryCommunity, SearchCache

from tgscore.dispersy.endpoint import StandaloneEndpoint, TunnelEndpoint
from tgscore.dispersy.callback import Callback
from tgscore.dispersy.dispersy import Dispersy
from tgscore.dispersy.member import Member
from tgscore.dispersy.crypto import (ec_generate_key,
        ec_to_public_bin, ec_to_private_bin)
from tgscore.square.community import PreviewCommunity, SquareCommunity

from TGSSignals import TGSSearchSignal, TGSNewCommunitySignal


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
        self.dispersy = None
        self.swift_process = None
        self._discovery = None
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
        logger.info("Searching members for:", search_terms)
        self._discovery.simple_member_search(search_terms, self.memberSearchUpdate.emit)

    def startNewSquareSearch(self, search_terms):
        logger.info("Searching squares for:", search_terms)
        self._discovery.simple_square_search(search_terms, self.squareSearchUpdate.emit)

    def startNewTextSearch(self, search_terms):
        logger.info("Searching text messages for:", search_terms)
        self._discovery.simple_text_search(search_terms, self.textSearchUpdate.emit)

    def joinSquare(self, square):
        self.callback.register(square.join_square)

    def leaveSquare(self, square):
        self.callback.register(square.leave_square)

    ##################################
    #Public methods:
    ##################################
    def rawserver_fatalerrorfunc(self, e):
        """ Called by network thread """
        logger.exception("tlm: RawServer fatal error func called", e)
        #print_exc()

    def rawserver_nonfatalerrorfunc(self, e):
        """ Called by network thread """
        logger.exception("tlm: RawServer non fatal error func called", e)
        #print_exc()

    def rawserver_keepalive(self):
        """ Hack to prevent rawserver sleeping in select() for a long time, not
        processing any tasks on its queue at startup time

        Called by network thread """
        self.rawserver.add_task(self.rawserver_keepalive, 1)
        
    def setupThreads(self):
        config = AndroidFacade.getConfig()
        AndroidFacade.monitor('config: {}'.format(config.toString()))
        """ (old method using singletons which is no longer supported)
        # start Dispersy
        dispersy = Dispersy.get_instance(callback, self._workdir)
        if AndroidFacade.getConfig().isDispersyEnabled():
            AndroidFacade.monitor('TGS: starting dispersy endpoint')
            dispersy.endpoint = StandaloneEndpoint(dispersy, AndroidFacade.getConfig().getDispersyPort())
            dispersy.endpoint.start()

        # updated (currently devel) dispersy startup sequence BEGINS HERE
        if config['dispersy']:
        """        
        if config.isDispersyEnabled():
            """
            self.sessdoneflag = Event()
            # get the tribler rawserver, I guess..
            # FIXME hardcodes defaults from https://github.com/Tribler/tribler/blob/devel/Tribler/Core/defaults.py
            self.rawserver = RawServer(self.sessdoneflag,
                                       #config['timeout_check_interval'],
                                       60.0,
                                       #config['timeout'],
                                       300.0,
                                       #ipv6_enable=config['ipv6_enabled'],
                                       0,
                                       failfunc=self.rawserver_fatalerrorfunc,
                                       errorfunc=self.rawserver_nonfatalerrorfunc)
            self.rawserver.add_task(self.rawserver_keepalive, 1)
            """
            # new database stuff will run on only one thread
            self.callback = Callback("Dispersy")  # WARNING NAME SIGNIFICANT
            
            # TODO support all permutations of proxy and swift
            # TODO find out if swift can be proxied
            # set communication endpoint
            if config.isTunnelDispersyOverSwift() and self.swift_process:
                AndroidFacade.monitor('starting TunnelEndpoint via swift')
                endpoint = TunnelEndpoint(self.swift_process)
                self.swift_process.add_download(endpoint)
            else:
                AndroidFacade.monitor('starting StandaloneEndpoint on port {}'.format(config.getDispersyPort()))
                #endpoint = RawserverEndpoint(self.rawserver, config.getDispersyPort())
                endpoint = StandaloneEndpoint(config.getDispersyPort())

            # from os.environ['ANDROID_PRIVATE']
            #working_directory = unicode(config['state_dir'])

            self.dispersy = Dispersy(self.callback, endpoint, self._workdir)

            # from PC version: TODO: see if we can postpone dispersy.start to improve GUI responsiveness.
            # However, for now we must start self.dispersy.callback before running
            # try_register(nocachedb, self.database_thread)!
            # ERK - not a problem for Android since the main UI thread is not blocked by the python threads
            AndroidFacade.monitor('starting dispersy')
            # FIXME I guess this implicitly starts the thread? ie callback.start()
            # FIXME doesn't seem to like the StandaloneEndpoint or RawserverEndpoint -- try endpoint.start()?
            self.dispersy.start()
            logger.info("lmc: Dispersy is listening on port", self.dispersy.wan_address[1], "[%d]" % id(self.dispersy))
            AndroidFacade.monitor('initializing dispersy')
            self.callback.register(self._dispersy)
        else:
            # new database stuff will run on only one thread
            AndroidFacade.monitor('dispersy disabled, not making an endpoint')
            self.callback = Callback("Dispersy")  # WARNING NAME SIGNIFICANT
            self.callback.start()
            
    def _dispersy(self):
        # load/join discovery community
        # this is the hardcoded key of the TGS app (aka "App ID")
        # do not change or you'll be a different app :)
        public_key = "3081a7301006072a8648ce3d020106052b81040027038192000406b34f060c416e452fd31fb1770c2f475e928effce751f2f82565bec35c46a97fb8b375cca4ac5dc7d93df1ba594db335350297f003a423e207b53709e6163b7688c0f60a9cf6599037829098d5fbbfe786e0cb95194292f241ff6ae4d27c6414f94de7ed1aa62f0eb6ef70d2f5af97c9aade8266eb85b14296ed2004646838c056d1d9ad8a509b69f81fbc726201b57".decode("HEX")
        master = self.dispersy.get_member(public_key)
        try:
            self._discovery = DiscoveryCommunity.load_community(self.dispersy, master)
        except ValueError:
            # generate user ID
            # FIXME allow generation of new ID from app settings screen (eg TOANFO)
            ec = ec_generate_key(u"low")
            self._my_member = Member(ec_to_public_bin(ec), ec_to_private_bin(ec))
            self._discovery = DiscoveryCommunity.join_community(self.dispersy, master, self._my_member)
        else:
            self._my_member = self._discovery.my_member

        self.dispersy.define_auto_load(PreviewCommunity, (self._discovery, False))
        self.dispersy.define_auto_load(SquareCommunity, (self._discovery,))

    	AndroidFacade.monitor('TGS: loading squares')
    	
        # load squares (ie master square community)
        # commented out code to send list since dispersy seems to send its own event for each square
        #communityList = self._TGSCommunityList()
        #listEvent = self._TGSListEvent()
        for master in SquareCommunity.get_master_members(self.dispersy):
            yield 0.1
            c = self.dispersy.get_community(master.mid)
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
        # FIXME probably not the right way to get dispersy
        if self.dispersy is None:
            return None
        return self.dispersy.get_community(cid)
    
    ##################################
    #Private methods:
    ##################################    
    def _dispersy_onSearchResult(self, result):
        logger.info("OnSearchResult", result)

    def _dispersyCreateCommunity(self, title, description, avatar, lat, lon, radius):
        community = SquareCommunity.create_community(self._my_member, self._discovery)
        community.set_square_info(title, description, avatar, (int(lat*10**6), int(lon*10**6)), radius)
