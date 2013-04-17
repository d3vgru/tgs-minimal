import os
import sys
import time

from jnius import autoclass
from jnius import cast

import AndroidFacade


def unicode_value_of(str):
    if isinstance(str, unicode):
        return str
    return unicode(str, 'utf-8')


class MainLoop():
    def __init__(self):
        self.go = True
        self._chatCore = None
    def setChatCore(self, chatCore):
        self._chatCore = chatCore
    def run(self):
        """ redirect stderr to our own stream?
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
        # read 1K from stderr and cc to monitor?
        errMsg = os.read(self.errs, 1024)
        if errMsg is not None:
            AndroidFacade.monitor(errMsg)
        """
        # process the next event in the queue from the Java side
        #f = AndroidFacade.getMainActivity()
        #AndroidFacade.monitor('MainLoop: TICK, {} events in queue'.format(f.queueSize()))
        nextEvent = AndroidFacade.nextEvent()
        if nextEvent is not None:
            eventClassName = nextEvent.getClass().getName()
            concreteEvent = cast(eventClassName, nextEvent)
            superEvent = cast('org.theglobalsquare.framework.TGSEvent', nextEvent)
#            AndroidFacade.monitor('MainLoop: got event from java, class: {}'.format(eventClass))
            if eventClassName == 'org.theglobalsquare.framework.values.TGSConfigEvent':
                # update config using latest values
                # hmm, subject seems to work here
                self._chatCore.setConfig(concreteEvent.getSubject())
            elif eventClassName == 'org.theglobalsquare.framework.values.TGSCommunitySearchEvent':
                communityObj = superEvent.getObject()
                if communityObj is not None:
                    terms = unicode_value_of(communityObj.getName())
                    # FIXME figure out how to handle unicode
                    AndroidFacade.monitor(u'ChatCore: got community search terms: {}'.format(terms))
                    # really start search
                    self._chatCore.startNewSquareSearch(terms)
            elif eventClassName == 'org.theglobalsquare.framework.values.TGSCommunityEvent':
                # subject works as long as we're the main thread
                subjectObj = cast('org.theglobalsquare.framework.values.TGSCommunity', concreteEvent.getSubject())
                
                # create/join/leave the community
                TGSCommunity = AndroidFacade.Community()
                verbObj = concreteEvent.getVerb()
                if TGSCommunity.LEAVE == verbObj:
                    # get cid and look up square
                    square = self._chatCore.getSquareForCid(subjectObj.getCid())
                    self._chatCore.joinSquare(square)
                    AndroidFacade.monitor('leave square: {}'.format(subjectObj.getName()))
                elif TGSCommunity.JOIN == verbObj:
                    # get cid and look up square
                    square = self._chatCore.getSquareForCid(subjectObj.getCid())
                    self._chatCore.leaveSquare(square)
                    AndroidFacade.monitor('join square: {}'.format(subjectObj.getName()))
                elif TGSCommunity.CREATE == verbObj:
                    # get name and description and create
                    name = unicode_value_of(subjectObj.getName())
                    description = unicode_value_of(subjectObj.getDescription())

                    # TODO support choosing avatar on the Android Side
                    #TODO: Publish the avatar via swift and set the avatar's hash here
                    avatar = ''

                    # TODO? support choosing coordinates/GPS on the Android side
                    lat = 0
                    lon = 0
                    radius = 1
                    
                    square_info = (name, description, avatar, lat, lon, radius)
                    self._chatCore.createNewSquare(square_info)
                    AndroidFacade.monitor('create square: {}'.format(name))
                else: AndroidFacade.monitor('did not recognize verb "{}"'.format(verbObj))
                
        # process up to 10 events per second
        time.sleep(.1)
        return self.go
