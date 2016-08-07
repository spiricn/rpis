import urllib


class Module:
    def __init__(self, name):
        self._name = name

    @property
    def name(self):
        return self._name

    @property
    def url(self):
        return urllib.parse.quote_plus(self._name)

    def getRestAPI(self):
        return ()