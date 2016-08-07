manifest = {
    'pages' : [
        {'file' : 'Index.html', 'pattern' : '^/index'},
        {'file' : '404.html'},
        {'file' : 'modules/ModuleStatus.html', 'pattern' : '^\\/Status'},
        {'file' : 'modules/ModulePower.html', 'pattern' : '^\\/Power'},
        {'file' : 'modules/ModuleStrip.html', 'pattern' : '^\\/Strip'},
        {'file' : 'modules/ModuleRest.html', 'pattern' : '^\\/Rest'},
    ],

    'home_page' : 'modules/ModuleStatus.html',

    '404_page' : '404.html',

    'favicon' : 'favicon.png',
}
