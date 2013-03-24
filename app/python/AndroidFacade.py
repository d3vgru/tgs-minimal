from jnius import autoclass
from jnius import cast


# pyjnius bindings to java framework
PythonActivity = autoclass('org.kivy.android.PythonActivity')
TGSMessage = autoclass('org.theglobalsquare.framework.values.TGSMessage')
TGSSystemEvent = autoclass('org.theglobalsquare.framework.values.TGSSystemEvent')
""" don't need yet
TGSEventProxy = autoclass('org.theglobalsquare.framework.TGSEventProxy')
TGSCommunity = autoclass('org.theglobalsquare.framework.values.TGSCommunity')
TGSCommunityEvent = autoclass('org.theglobalsquare.framework.values.TGSCommunityEvent')
TGSConfig = autoclass('org.theglobalsquare.framework.values.TGSConfig')
TGSConfigEvent = autoclass('org.theglobalsquare.framework.values.TGSConfigEvent')
TGSMessageEvent = autoclass('org.theglobalsquare.framework.values.TGSMessageEvent')
TGSUser = autoclass('org.theglobalsquare.framework.values.TGSUser')
TGSUserEvent = autoclass('org.theglobalsquare.framework.values.TGSUserEvent')
TGSCommunitySearchEvent = autoclass('org.theglobalsquare.framework.values.TGSCommunitySearchEvent')
TGSMessageSearchEvent = autoclass('org.theglobalsquare.framework.values.TGSMessageSearchEvent')
TGSUserSearchEvent = autoclass('org.theglobalsquare.framework.values.TGSUserSearchEvent')
"""

class AndroidFacade:
    @staticmethod
    def getMainActivity():
    	return cast('org.theglobalsquare.app.TGSMainActivity', PythonActivity.mActivity)

    @staticmethod
    def sendEvent(event):
        return AndroidFacade.getMainActivity().sendEvent(event)
        
    @staticmethod
    def nextEvent():
        return AndroidFacade.getMainActivity().getEvents().nextEvent()

    @staticmethod
    def monitor(msg):
        message = TGSMessage()
        message.setBody(msg)
        event = TGSSystemEvent.forLog(message)
        AndroidFacade.sendEvent(event)
