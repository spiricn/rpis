class ModuleManager:
    def __init__(self, engine):
        self._engine = engine
        self._modules = {}

    @property
    def engine(self):
        return self._engine

    def registerModule(self, moduleClass):
        obj = moduleClass(self)

        self._modules[obj.name] = obj

        api = obj.getRestAPI()
        if api:
            self.engine.rest.addApi(api)

    def stop(self):
        for name, module in self._modules.items():
            module.stopModule()

    @property
    def modules(self):
        return self._modules.values()

    def get(self, name):
        return self._modules[name]
