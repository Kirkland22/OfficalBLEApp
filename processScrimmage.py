
import json
import sys
import os
import glob
import re

RECEIVE_TIME = 0
PACKET_TYPE = 1
SAVE_NAME = ""
teams = {}



# Converts HH:MM:SS(String) format to seconds(int) then calculates latancy
def calculatePacketLatancy(sent,recieved):
	sent = sent.split(">")[1].split(":")
	recieved = recieved.split(":")
	
	sentTimeSec = (int(sent[0]) *3600) + (int(sent[1]) * 60) + float(sent[2])
	recievedTimeSec = (int(recieved[0]) * 3600) + (int(recieved[1]) * 60) + float(recieved[2])
	
	return (recievedTimeSec - sentTimeSec)


def writeToCSV(srcTeam,srcIP,dstTeam,dstIP,latancy,throughput,numOfPackets):
	csv = open(SAVE_NAME, "a")
	csv.write(srcTeam + "," + srcIP + "," + dstTeam + "," + dstIP + "," + latancy + "," + throughput + "," + numOfPackets + "\n")
	csv.close()

def determineTeamNodes(logFile):

	# Regex Search for "SENDNODE-(SENDING TEAM NUMBER HERE)_" and "RECNODE-(DST TEAM NUMBER HERE)_
	srcTeamNumber = (re.search('(?<=SENDNODE-)\w{1,2}(?=_)', logFile)).group(0)
	dstTeamNumber = (re.search('(?<=RECNODE-)\w{1,2}(?=_)', logFile)).group(0)
	srcTeam = TEAMS[srcTeamNumber]
	dstTeam = TEAMS[dstTeamNumber]

	return (srcTeam , dstTeam)

def parseLog(logFile):
	totalLatancy = 0
	totalThruPut = 0
	numOfReceivedPackets  = 0
	srcIP = "0"
	dstIP = "0"
	srcTeam , dstTeam = determineTeamNodes(logFile)
	
	SOURCE_IP = 5
	DESTANTION_IP = 6
	SEND_TIME = 7
	SIZE = 8

	with open(logFile,"r") as nodeLog:
		for line in nodeLog:
			packetInfo = line.split(" ")
			
			# Some log files have an extra column(frag), if they do, the position of the other columns are shifted to the right by 1				
			if (len(packetInfo) == 12):
				SOURCE_IP = 6
				DESTANTION_IP = 7
				SEND_TIME = 8
				SIZE = 9
			
			# Check if packet was recieved
			if packetInfo[PACKET_TYPE] == "RECV":

				srcIP = packetInfo[SOURCE_IP].split(">")[1]
				dstIP = packetInfo[DESTANTION_IP].split(">")[1]	
				packetSize = int(packetInfo[SIZE].split(">")[1])
				packetLatancy = calculatePacketLatancy(packetInfo[SEND_TIME],packetInfo[RECEIVE_TIME])
				numOfReceivedPackets  += 1
				totalLatancy += packetLatancy
				totalThruPut += (packetLatancy / packetSize)
		
	if (numOfReceivedPackets !=0 ):
		averageLatancy = totalLatancy/numOfReceivedPackets
		averageThroughput = totalThruPut / numOfReceivedPackets
		writeToCSV(srcTeam,srcIP,dstTeam,dstIP, str(averageLatancy) , str(averageThroughput), str(numOfReceivedPackets) )


def createCSV(match):
	
	global SAVE_NAME 
	SAVE_NAME =  match + ".csv"
	csv = open(SAVE_NAME,'w')
	csv.write("Team Sending, Source IP, Team Receiving, Destination IP, Latancy (Seconds), Throughput(Bits/sec), # Packets Sent\n")
	csv.close()

#Loads Team info from JSON file to be mapped which nodes belong to which teams
def getTeamList():
	
	global TEAMS
	TEAMS = {}
	teamList = glob.glob("Match*.json")[0]
	
	with open(teamList) as json_file:
		json_data = json.load(json_file)
		for node in json_data["NodeData"]:
			TEAMS[str(node["RFNode_ID"])] = node["ImageName"]
		
		print TEAMS


# Creates CSV for match then parses data 
def processMatch(match):
	
	os.chdir(match)		
	
	getTeamList()
	createCSV(match)

	for logFile in glob.glob("traffic_logs/listen*"):
		parseLog(logFile)
	#Back to scrim folder to find next match
	os.chdir("..")


def main():
	
	if (len(sys.argv) == 1) :
		print "USAGE - python parse.py {DIRECTORY OF SCRIM FOLDER}  "
		sys.exit()	
	dirPath = sys.argv[1]
	os.chdir(dirPath)
	
	for match in glob.glob("MATCH*"):
		processMatch(match)

	

if __name__ == '__main__':
    main()





