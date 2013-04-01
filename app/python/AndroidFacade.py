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

# bindings
def EventProxy():
    return autoclass('org.theglobalsquare.framework.TGSEventProxy')
    
def Message():
    return autoclass('org.theglobalsquare.framework.values.TGSMessage')
    
def SystemEvent():
    return autoclass('org.theglobalsquare.framework.values.TGSSystemEvent')

# main entry point
def getMainActivity():
    PythonActivity = autoclass('org.kivy.android.PythonActivity')
    return cast('org.theglobalsquare.app.TGSMainActivity', PythonActivity.mActivity)

# convenience function to send event
def sendEvent(event):
    return getMainActivity().sendEvent(event)
    #pass
	
# convenience function to get next event
def nextEvent():
    return getMainActivity().getEvents().nextEvent()
    #pass

# convenience logging function
def monitor(msg):
    TGSMessage = Message()
    message = TGSMessage()
    message.setBody(msg)
    TGSSystemEvent = SystemEvent()
    event = TGSSystemEvent.forLog(message)
    sendEvent(event)
    #pass
