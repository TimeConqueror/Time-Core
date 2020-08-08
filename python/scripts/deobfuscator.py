from argparse import ArgumentParser
from util.main import parse_properties
from util.main import PROJECT_PROPERTIES_PATH
import sys
import os

MAPPINGS_LIST_KEY = "deobf_mappings_list"

parser = ArgumentParser(add_help=False)
parser.add_argument('--paths', action="store")

# args = parser.parse_args(sys.argv)
args = parser.parse_args(sys.argv[1:])

print(args)
paths = str(args.__getattribute__("paths")).split(",")

build_props = parse_properties(PROJECT_PROPERTIES_PATH)
if MAPPINGS_LIST_KEY not in build_props.keys():
    pass

mappings_list = build_props[MAPPINGS_LIST_KEY].split(',')
mappings_list = [mappings for mappings in mappings_list if not mappings.strip() == ""]

for path in paths:
    path = path.strip()
    for mappings in mappings_list:
    mappings = mappings.strip()
        os.system("java -jar ../BON-2.4.0.15-all.jar " +
                  "--inputJar \"" + path + "\" "
                  "--outputJar \"" + path.replace(".jar", "") + "-deobf-" + mappings + ".jar\" " +
                  "--mappingsVer " + mappings)
