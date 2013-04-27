from jnius import autoclass
from jnius import cast


# pyjnius bindings to java framework

# interfaces
def ActivityInterface():
    return autoclass('org.theglobalsquare.framework.ITGSActivity')

def FacadeInterface():
    return autoclass('org.theglobalsquare.framework.ITGSFacade')

def ListInterface():
    return autoclass('org.theglobalsquare.framework.ITGSList')

def ObjectInterface():
    return autoclass('org.theglobalsquare.framework.ITGSObject')
    
# main entry point
def getMainActivity():
    PythonActivity = autoclass('org.kivy.android.PythonActivity')
    MainActivity = autoclass('org.theglobalsquare.app.TGSMainActivity')
    return cast('org.theglobalsquare.app.TGSMainActivity', PythonActivity.mActivity)

# concrete Facade, for calling static methods
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

def ListEvent():
    return autoclass('org.theglobalsquare.framework.TGSListEvent')
    
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
def getTGSFacade():
    PythonActivity = autoclass('org.kivy.android.PythonActivity')
    # WTF: init TGSConfig class or reflect fails
    Config()
    BaseActivity = cast('org.theglobalsquare.framework.activity.TGSBaseActivity', PythonActivity.mActivity)
    return BaseActivity.getTGSFacade()

# convenience method to get config
def getConfig():
    return getTGSFacade().getConfig()

# convenience function to send event
def sendEvent(event):
    tgsEvent = cast('org.theglobalsquare.framework.TGSEvent', event)
    return Facade().sendEvent(tgsEvent)
	
# convenience function to get next event
def nextEvent():
    return Facade().nextEvent()

# convenience logging function
def monitor(msg):
    TGSMessage = Message()
    message = TGSMessage()
    if not isinstance(msg, unicode):
        msg = unicode(msg, 'utf-8')
    message.setBody(msg)
    TGSSystemEvent = SystemEvent()
    event = TGSSystemEvent.forLog(message)
    sendEvent(event)

# config
def getAlias():
    return getTGSFacade().getAlias()
    
def isProxyEnabled():
    return getTGSFacade().isProxyEnabled()

def isProxyRequired():
    return getTGSFacade().isProxyRequired()

def getProxyHost():
    return getTGSFacade().getProxyHost()

def getProxyPort():
    return getTGSFacade().getProxyPort()
