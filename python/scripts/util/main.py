import os
import pathlib

DATA_PROPERTIES_PATH = "../data.properties"
PROJECT_PROPERTIES_PATH = "../../build.properties"

RELEASE_KEY = "timecore.release_version"
BUILD_VERSION_KEY = "timecore.build_version"


def update_version_if_required():
    storage = parse_properties(DATA_PROPERTIES_PATH)
    project_props = parse_properties(PROJECT_PROPERTIES_PATH)

    check_version_change(storage, project_props[RELEASE_KEY])

    write_properties(DATA_PROPERTIES_PATH, storage)


def increment_build_version():
    storage = parse_properties(DATA_PROPERTIES_PATH)
    project_props = parse_properties(PROJECT_PROPERTIES_PATH)

    check_version_change(storage, project_props[RELEASE_KEY])

    prev_build_version = int(storage[BUILD_VERSION_KEY])
    new_build_version = str(prev_build_version + 1)

    storage[BUILD_VERSION_KEY] = new_build_version

    write_properties(DATA_PROPERTIES_PATH, storage)

    pass


def check_version_change(storage, new_version):
    if RELEASE_KEY not in storage.keys():
        storage[RELEASE_KEY] = "not_defined"
    if BUILD_VERSION_KEY not in storage.keys():
        storage[BUILD_VERSION_KEY] = "0"

    if storage[RELEASE_KEY] != new_version:
        storage[RELEASE_KEY] = new_version
        storage[BUILD_VERSION_KEY] = str(1)


def write_properties(path, properties):
    with open(path, "w") as file:
        for prop in properties.items():
            file.write(prop[0] + "=" + prop[1] + "\n")
    pass


def parse_properties(path):

    with open(path) as file:
        properties = dict()
        lines = file.read().splitlines()

        for line in lines:
            if "=" in line:
                key_value_pair = line.split("=")
                properties[key_value_pair[0]] = key_value_pair[1]

    return properties
