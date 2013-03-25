from jnius import autoclass
from jnius import cast


# pyjnius bindings to java framework
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

def getMainActivity():
    PythonActivity = autoclass('org.kivy.android.PythonActivity')
	return cast('org.theglobalsquare.app.TGSMainActivity', PythonActivity.mActivity)

def sendEvent(event):
	return getMainActivity().sendEvent(event)
	
def nextEvent():
	return getMainActivity().getEvents().nextEvent()

def monitor(msg):
    TGSMessage = autoclass('org.theglobalsquare.framework.values.TGSMessage')
    message = TGSMessage()
	message.setBody(msg)
	TGSSystemEvent = autoclass('org.theglobalsquare.framework.values.TGSSystemEvent')
	event = TGSSystemEvent.forLog(message)
	sendEvent(event)
