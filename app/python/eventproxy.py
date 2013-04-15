#
# -*- coding: utf-8 -*-

import AndroidFacade

from TGSSignals import TGSNewCommunitySignal

from tgscore.square.community import SquareCommunity, PreviewCommunity

_global_broker = None

def createEventBroker(obj):
    global _global_broker
    if not _global_broker:
        _global_broker = TGSGlobalEventBroker()
    return _global_broker

class TGSGlobalEventBroker:
    def __init__(self):
        TGSCommunityEvent = AndroidFacade.CommunityEvent()
        self._newSquareUpdateEvent = TGSCommunityEvent
        self._newSquareUpdate = TGSNewCommunitySignal(self._newSquareUpdateEvent)

    #TODO: use __gettattr__ for this.
    def newCommunityCreated(self, square):
        #self._tgs.newCommunityCreated.emit(square)
        self._newSquareUpdate.emit(square)
    def newPreviewCommunityCreated(self, square):
        # do we need this?
        #self._tgs.newPreviewCommunityCreated.emit(square)
        pass
    def newHotCommunitiesAvailable(self, squares, texts):
        # do we need this?
        #self._tgs.newHotCommunitiesAvailable.emit(squares, texts)
        pass
