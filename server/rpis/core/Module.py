import urllib


class Module:
    def __init__(self, manager, name):
        self._name = name
        self._manager = manager

    @property
    def manager(self):
        return self._manager

    @property
    def name(self):
        return self._name

    @property
    def url(self):
        return urllib.parse.quote_plus(self._name)

    def getRestAPI(self):
        return ()

    def stopModule(self):
        raise NotImplementedError()
