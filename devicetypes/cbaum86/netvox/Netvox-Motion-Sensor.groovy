/**  
 *  Netvox Motion Sensor ZB01D  
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Thanks to Seth Jansen @sjansen and Gilbert Chan @thegilbertchan for original contributions
 */
metadata {
    definition (name: "Netvox Dimmer Switch", namespace: "chrisb", author: "Chris Baum") {
        capability "Button"
        capability "Configuration"
                
        attribute "button2","ENUM",["released","pressed"]

        fingerprint endpointId: "01", profileId: "0104", manufacturer: "netvox", model: "ZB02FE0ED", deviceJoinName: "Netvox Dimmer Switch", inClusters: "0000, 0001, 0003, 0015 0406", outClusters: ""
    
    }
    // Contributors

    // simulator metadata
    simulator {
    }

    // UI tile definitions
    tiles(scale: 2) {
        multiAttributeTile(name: "motion", type: "generic", width: 6, height: 4) {
            tileAttribute("device.motion", key: "PRIMARY_CONTROL") {
                attributeState "active", label: 'motion', icon: "st.motion.motion.active", backgroundColor: "#00A0DC"
                attributeState "inactive", label: 'no motion', icon: "st.motion.motion.inactive", backgroundColor: "#cccccc"
            }
        }
        main(["motion"])
        details(["motion"])
    }
}

// Parse incoming device messages to generate events

def parse(String description) {
    log.debug "Parse description $description"
    def name = null
    def value = null              
    if (description?.startsWith("catchall: 0104 0406 01")) {
        name = "1"
        def currentST = device.currentState("button")?.value
        log.debug "Motion Detected"           
            
    }

    def result = createEvent(name: "button", value: "pushed", data: [buttonNumber: name], descriptionText: "$device.displayName button $name was pushed", isStateChange: true)
    log.debug "Parse returned ${result?.descriptionText}"


    return result
}

def parseDescriptionAsMap(description) {
    (description - "read attr - ").split(",").inject([:]) { map, param ->
        def nameAndValue = param.split(":")
        map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
    }
}

private getFPoint(String FPointHex){                    // Parsh out hex string from Value: 4089999a
    Long i = Long.parseLong(FPointHex, 16)              // Convert Hex String to Long
    Float f = Float.intBitsToFloat(i.intValue())        // Convert IEEE 754 Single-Precison floating point
    log.debug "converted floating point value: ${f}"
    def result = f

    return result
}


// Commands to device

def configure() { 
    log.debug "Binding SEP 0x01 DEP 0x01 Cluster 0x0406 Occupancy cluster to hub"         
    def configCmds = [

    // Bind the occupancy cluster
    "zdo bind 0x${device.deviceNetworkId} 0x01 0x01 0x0406 {${device.zigbeeId}} {}", "delay 500",
    ]
    log.info "Sending ZigBee Bind commands to Dimmer Switch"
    return configCmds
}