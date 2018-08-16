#!/bin/bash
# Yet Another UserAgent Analyzer
# Copyright (C) 2013-2018 Niels Basjes
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
TARGETDIR="${SCRIPTDIR}/../../../resources/UserAgents"

INPUT=OperatingSystemDeviceNames.csv
OUTPUT="${TARGETDIR}/OperatingSystemDeviceNames.yaml"

if [ "Generate.sh" -ot "${OUTPUT}" ]; then
    if [ "${INPUT}" -ot "${OUTPUT}" ]; then
        echo "Up to date: ${OUTPUT}";
        exit;
    fi
fi

echo "Generating: ${OUTPUT}";

(
echo "# ============================================="
echo "# THIS FILE WAS GENERATED; DO NOT EDIT MANUALLY"
echo "# ============================================="
echo "#"
echo "# Yet Another UserAgent Analyzer"
echo "# Copyright (C) 2013-2018 Niels Basjes"
echo "#"
echo "# Licensed under the Apache License, Version 2.0 (the \"License\");"
echo "# you may not use this file except in compliance with the License."
echo "# You may obtain a copy of the License at"
echo "#"
echo "# https://www.apache.org/licenses/LICENSE-2.0"
echo "#"
echo "# Unless required by applicable law or agreed to in writing, software"
echo "# distributed under the License is distributed on an \"AS IS\" BASIS,"
echo "# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
echo "# See the License for the specific language governing permissions and"
echo "# limitations under the License."
echo "#"
echo "config:"

echo "- set:"
echo "    name: 'OSPatterns'"
echo "    values:"
fgrep -v '#' "${INPUT}" | grep . | sed 's/ *|/|/g;s/| */|/g' | while read line
do
    ospattern=$(echo "${line}" | cut -d'|' -f1)
    echo "    - '${ospattern}'"
done
echo ""

fgrep -v '#' "${INPUT}" | grep . | sed 's/ *|/|/g;s/| */|/g' | while read line
do
    ospattern=$(echo "${line}" | cut -d'|' -f1)
    osname=$(   echo "${line}" | cut -d'|' -f2)
    devclass=$( echo "${line}" | cut -d'|' -f3)
    devname=$(  echo "${line}" | cut -d'|' -f4)
    devbrand=$( echo "${line}" | cut -d'|' -f5)
    osclass=$(  echo "${line}" | cut -d'|' -f6)
echo "
# =======================================================================
# ${ospattern}

- matcher:
    extract:
    - 'DeviceClass                         :    111 :\"${devclass}\"'
    - 'DeviceName                          :    111 :\"${devname}\"'
    - 'DeviceBrand                         :    111 :\"${devbrand}\"'
    - 'OperatingSystemClass                :    150 :\"${osclass}\"'
    - 'OperatingSystemName                 :    150 :\"${osname}\"'
    - 'OperatingSystemVersion              :    150 :CleanVersion[agent.(1)product.(1)comments.entry.product.name=\"${ospattern}\"^.(1)version]'

# Only if the second version field is NOT a type of CPU.
- matcher:
    variable:
    - 'Version: agent.(1)product.(1)comments.entry.product.name=\"${ospattern}\"^.(2)version'
    require:
    - 'IsNull[LookUp[CPUArchitectures;@Version]]'
    extract:
    - 'DeviceClass                         :    111 :\"${devclass}\"'
    - 'DeviceName                          :    111 :\"${devname}\"'
    - 'DeviceBrand                         :    111 :\"${devbrand}\"'
    - 'OperatingSystemClass                :    150 :\"${osclass}\"'
    - 'OperatingSystemName                 :    150 :\"${osname}\"'
    - 'OperatingSystemVersion              :    151 :CleanVersion[@Version]'

- matcher:
    require:
    - 'agent.product.(1)comments.entry.text=\"${ospattern}\"'
    extract:
    - 'DeviceClass                         :    111 :\"${devclass}\"'
    - 'DeviceName                          :    111 :\"${devname}\"'
    - 'DeviceBrand                         :    111 :\"${devbrand}\"'
    - 'OperatingSystemClass                :    151 :\"${osclass}\"'
    - 'OperatingSystemName                 :    151 :\"${osname}\"'
    - 'OperatingSystemVersion              :    149 :\"??\"'

- matcher:
    extract:
    - 'DeviceClass                         :    111 :\"${devclass}\"'
    - 'DeviceName                          :    111 :\"${devname}\"'
    - 'DeviceBrand                         :    111 :\"${devbrand}\"'
    - 'OperatingSystemClass                :    150 :\"${osclass}\"'
    - 'OperatingSystemName                 :    150 :\"${osname}\"'
    - 'OperatingSystemVersion              :    150 :CleanVersion[agent.product.name=\"${ospattern}\"^.(1)version]'

# Only if the second version field is NOT a type of CPU.
- matcher:
    variable:
    - 'Version: agent.product.name=\"${ospattern}\"^.(2)version'
    require:
    - 'IsNull[LookUp[CPUArchitectures;@Version]]'
    extract:
    - 'DeviceClass                         :    111 :\"${devclass}\"'
    - 'DeviceName                          :    111 :\"${devname}\"'
    - 'DeviceBrand                         :    111 :\"${devbrand}\"'
    - 'OperatingSystemClass                :    150 :\"${osclass}\"'
    - 'OperatingSystemName                 :    150 :\"${osname}\"'
    - 'OperatingSystemVersion              :    151 :CleanVersion[@Version]'

- matcher:
    require:
    - 'agent.product.name[-1]=\"${ospattern}\"'
    extract:
    - 'DeviceClass                         :    111 :\"${devclass}\"'
    - 'DeviceName                          :    111 :\"${devname}\"'
    - 'DeviceBrand                         :    111 :\"${devbrand}\"'
    - 'OperatingSystemClass                :    152 :\"${osclass}\"'
    - 'OperatingSystemName                 :    152 :\"${osname}\"'
    - 'OperatingSystemVersion              :    149 :\"??\"'

- matcher:
    require:
    - 'agent.product.name[-2]=\"${ospattern}\"'
    extract:
    - 'DeviceClass                         :    112 :\"${devclass}\"'
    - 'DeviceName                          :    112 :\"${devname}\"'
    - 'DeviceBrand                         :    112 :\"${devbrand}\"'
    - 'OperatingSystemClass                :    153 :\"${osclass}\"'
    - 'OperatingSystemName                 :    153 :\"${osname}\"'
    - 'OperatingSystemVersion              :    149 :\"??\"'

"
done

) > ${OUTPUT}