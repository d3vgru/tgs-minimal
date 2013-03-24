#__all__ = ('NullLayout', )

from kivy.uix.layout import Layout

class NullLayout(Layout):
    def __init__(self, **kwargs):
        super(NullLayout, self).__init__(**kwargs)

