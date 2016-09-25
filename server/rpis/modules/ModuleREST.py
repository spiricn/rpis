from rpis.core.Module import Module


class ModuleREST(Module):
    def __init__(self, manager):
        Module.__init__(self, manager, 'Rest API')

    def stopModule(self):
        pass