from jnius import autoclass
from jnius import cast


# pyjnius bindings to java framework
""" don't need yet
TGSMessageSearchEvent = autoclass('org.theglobalsquare.framework.values.TGSMessageSearchEvent')
TGSUserSearchEvent = autoclass('org.theglobalsquare.framework.values.TGSUserSearchEvent')
"""

# model bindings
def Config():
    return autoclass('org.theglobalsquare.framework.values.TGSConfig')

def Community():
    return autoclass('org.theglobalsquare.framework.values.TGSCommunity')

def EventProxy():
    return autoclass('org.theglobalsquare.framework.TGSEventProxy')
    
def Message():
    return autoclass('org.theglobalsquare.framework.values.TGSMessage')
    
def User():
    return autoclass('org.theglobalsquare.framework.values.TGSUser')

# event bindings
def CommunityEvent():
    return autoclass('org.theglobalsquare.framework.values.TGSCommunityEvent')

def ConfigEvent():
    return autoclass('org.theglobalsquare.framework.values.TGSConfigEvent')

def MessageEvent():
    return autoclass('org.theglobalsquare.framework.values.TGSMessageEvent')

def UserEvent():
    return autoclass('org.theglobalsquare.framework.values.TGSUserEvent')
    
def CommunitySearchEvent():
    return autoclass('org.theglobalsquare.framework.values.TGSCommunitySearchEvent')

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
