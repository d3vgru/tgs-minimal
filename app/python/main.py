import os
import sys

# ANDROID_PRIVATE is /data/data/[package name]/files
sys.path.append(os.environ['ANDROID_PRIVATE'] + '/tgs-core')

from ChatCore import ChatCore


# FIXME use new event model
# this is for async events like new square created, message received, community suggestion received
#Set up our QT event broker
#events.setEventBrokerFactory(eventproxy.createEventBroker)
#global_events = eventproxy.createEventBroker(None)


if __name__ == '__main__':
    exit_exception = None
    if exit_exception:
        raise exit_exception
    chat = ChatCore()
    chat.run()
