from jnius import autoclass
from jnius import cast


# pyjnius bindings to java framework
def Facade():
    return autoclass('org.theglobalsquare.app.Facade')

# model bindings
def Config():
    return autoclass('org.theglobalsquare.framework.values.TGSConfig')

def Community():
    return autoclass('org.theglobalsquare.framework.values.TGSCommunity')
    
def CommunityList():
    return autoclass('org.theglobalsquare.framework.values.TGSCommunityList')
    
def Event():
    return autoclass('org.theglobalsquare.framework.TGSEvent')
    
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
    
def MessageSearchEvent():
    return autoclass('org.theglobalsquare.framework.values.TGSMessageSearchEvent')

def SystemEvent():
    return autoclass('org.theglobalsquare.framework.values.TGSSystemEvent')

def UserSearchEvent():
    return autoclass('org.theglobalsquare.framework.values.TGSUserSearchEvent')

# convenience method to get underlying Facade (android.app.Application subclass)
def getFacade():
    PythonActivity = autoclass('org.kivy.android.PythonActivity')
    # WTF: init TGSConfig class or reflect fails
    Config()
    BaseActivity = cast('org.theglobalsquare.framework.activity.TGSBaseActivity', PythonActivity.mActivity)
    return BaseActivity.getFacade()

# convenience method to get config
def getConfig():
    return getFacade().getConfig()

# main entry point
def getMainActivity():
    PythonActivity = autoclass('org.kivy.android.PythonActivity')
    MainActivity = autoclass('org.theglobalsquare.app.TGSMainActivity')
    return cast('org.theglobalsquare.app.TGSMainActivity', PythonActivity.mActivity)

# convenience function to send event
def sendEvent(event):
    tgsEvent = cast('org.theglobalsquare.framework.TGSEvent', event)
    return Facade().sendEvent(tgsEvent)
	
# convenience function to get next event
def nextEvent():
    return getFacade().nextEvent()

# convenience logging function
def monitor(msg):
    TGSMessage = Message()
    message = TGSMessage()
    message.setBody(msg)
    TGSSystemEvent = SystemEvent()
    event = TGSSystemEvent.forLog(message)
    sendEvent(event)

# config
def getAlias():
    return getFacade().getAlias()
    
def isProxyEnabled():
    return getFacade().isProxyEnabled()

def isProxyRequired():
    return getFacade().isProxyRequired()

def getProxyHost():
    return getFacade().getProxyHost()

def getProxyPort():
    return getFacade().getProxyPort()
