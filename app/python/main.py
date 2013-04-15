import os
import sys

# ANDROID_PRIVATE is /data/data/[package name]/files
sys.path.append(os.environ['ANDROID_PRIVATE'] + '/tgs-core')

from ChatCore import ChatCore

import eventproxy

from tgscore import events


# this is for async events like new square created, community suggestion received
events.setEventBrokerFactory(eventproxy.createEventBroker)
global_events = eventproxy.createEventBroker(None)


if __name__ == '__main__':
    exit_exception = None
    if exit_exception:
        raise exit_exception
    chat = ChatCore()
    chat.run()
