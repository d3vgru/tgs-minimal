from jnius import autoclass
from jnius import cast

import AndroidFacade

# for simple notifications of a recurring event
class TGSSignal:
    def __init__(self, eventProtoClass):
        # dunno if we really have to do all this, but jnius loves dead chickens :)
        # if we do need this, will also need to add protos for users and messages and related events
        self._eventProtoClass = eventProtoClass
        self._objectProtoClass = autoclass('org.theglobalsquare.framework.ITGSObject')
        self._communityListProtoClass = autoclass('org.theglobalsquare.framework.values.TGSCommunityList')
        self._communityProtoClass = autoclass('org.theglobalsquare.framework.values.TGSCommunity')
    def emit(self, *argv, **kwargs):
# argv is something like (<tgscore.discovery.community.SearchCache object at 0x454ad050>, 'finished')
        event = self._eventProtoClass()
        cache = argv[0]
        community = self._communityProtoClass()

        # must cast as the exact type that formal param of setSubject() expects
        superSubject = cast('org.theglobalsquare.framework.ITGSObject', community)
        event.setSubject(superSubject)

        # TODO standardize verbs against activitystrea.ms
        event.setVerb(argv[1])

        # put hits in results
        hits = self._communityListProtoClass()
        for suggestion in cache.suggestions:
                square = suggestion.hit
                if suggestion.state == 'done':
                    hit = self._communityProtoClass()
                    # TODO migrate square data structure to TGSCommunity
                    hits.addCommunity(hit)
        superHits = cast('org.theglobalsquare.framework.ITGSObject', hits)
        event.setObject(superHits)
        
        # terms[0] is the (first set of?) terms
        AndroidFacade.monitor('Signal: terms[0]: {}'.format(cache.terms[0]))

        # terms[0][0] is the length of the shortest term
        # terms[0][1] is the name
        # get the name of the first term
        community.setName(cache.terms[0][1])

        AndroidFacade.sendEvent(event)

