import os
build_dir ='kits/build'
inkscape="C:\Program Files (x86)\Inkscape\Inkscape.exe"
sed="C:\cygwin64\bin\sed.exe"

def InkscapeBuilderGenerator(source, target, env, for_signature, width, height, export_area=""):
    cmd = INKSCAPE + '$SOURCES $TARGET -w %s -h %s" % variable % height
    if (export_area != "")
        cmd = cmd + " %s" % export_area
    return cmd

svg2png = Builder(generator=InkscapeBuilderGenerator)
env = DefaultEnvironment(ENV = {'PATH' : os.environ['PATH']}, BUILDERS = {"svg2png": svg2png})
env.Append(JAVACFLAGS = '-encoding utf-8')

env.svg2png()
#bld = Builder(action = inkscape + ' -f $SOURCE > $TARGET')
#env = Environment(BUILDERS = {'Foo' : bld})


Install(build_dir + "/resources", "resources/species.xml")
Install(build_dir + "/resources", "resources/example.kga")
Install(build_dir + "/resources", "resources/christian.kga")
Install(build_dir + "/resources/translation", Glob("resources/translation/*.xml"))
Install(build_dir + "/resources/pics", Glob("resources/pics/*.png"))

env.Java(target = build_dir, source = 'src')
env.Jar(target = 'kits/test.jar', source = Glob(build_dir + '/*') + [ 'resources/Manifest.txt' ], JARCHDIR=build_dir)
