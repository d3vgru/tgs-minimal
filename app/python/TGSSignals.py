from jnius import autoclass
from jnius import cast

import AndroidFacade

import TGS

# for when search results need to be returned
class TGSSearchSignal:
    def __init__(self, eventProtoClass):
        # dunno if we really have to do all this, but jnius loves dead chickens :)
        # if we do need this, will also need to add protos for users and messages and related events
        self._eventProtoClass = eventProtoClass
        self._listProtoClass = autoclass('org.theglobalsquare.framework.ITGSList')
        self._objectProtoClass = autoclass('org.theglobalsquare.framework.ITGSObject')

    def emit(self, *argv, **kwargs):
# argv is something like (<tgscore.discovery.community.SearchCache object at 0x454ad050>, 'finished')
        event = self._eventProtoClass()
        cache = argv[0]
        termsObject = event.emptyObject()
        
        # terms[0][0] is the length of the shortest term
        # terms[0][1] is the name
        # get the name of the first term
        termsObject.setName(cache.terms[0][1])

        # must cast as the exact type that formal param of setSubject() expects
        superSubject = cast('org.theglobalsquare.framework.ITGSObject', termsObject)
        event.setSubject(superSubject)

        # TODO standardize verbs against activitystrea.ms
        event.setVerb(argv[1])

        # put hits in results
        hits = event.emptyList()
        for suggestion in cache.suggestions:
                square = suggestion.hit
                if suggestion.state == 'done':
                    hit = event.emptyObject()
                    # TODO migrate square data structure to TGSCommunity
                    # TODO callback to do data copying
                    
                    superHit = cast('org.theglobalsquare.framework.ITGSObject', hit)
                    hits.add(superHit)
        superHits = cast('org.theglobalsquare.framework.ITGSObject', hits)
        event.setObject(superHits)
        
        # terms[0] is the (first set of?) terms
        AndroidFacade.monitor(u'Signal: terms[0]: {}'.format(cache.terms[0]))

        AndroidFacade.sendEvent(event)

# for when dispersy completes creating a new square
class TGSNewCommunitySignal:
    def __init__(self, eventProtoClass):
        self._eventProtoClass = eventProtoClass
        self._objectProtoClass = autoclass('org.theglobalsquare.framework.ITGSObject')

    def emit(self, *argv, **kwargs):
        event = self._eventProtoClass()

        square = argv[0]
        AndroidFacade.monitor(u'NewCommunitySignal: square: {}'.format(square))
        
        community = cast('org.theglobalsquare.framework.values.TGSCommunity', event.emptyObject())
        TGS.copySquareToCommunity(square, community)
        
        # must cast as the exact type that formal param of setSubject() expects
        superSubject = cast('org.theglobalsquare.framework.ITGSObject', community)
        event.setSubject(superSubject)
        
        event.setVerb(AndroidFacade.Community().CREATED)

        AndroidFacade.sendEvent(event)
