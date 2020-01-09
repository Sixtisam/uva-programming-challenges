import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.LinkedList;

/**
 * 10142 - Australian Voting
 * @date 05.01.2020
 * @author Samuel Keusch
 */
public class Main {
	// collect all output in a StringBuilder (its faster than System.out)
	public static final StringBuilder OUTPUT = new StringBuilder();

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (reader.ready()) {
				int testcaseCount = Integer.parseInt(reader.readLine().trim());
				reader.readLine(); // read the empty line
				
				for(int i = 0; i < testcaseCount; ++i){
					int candidateCount = Integer.parseInt(reader.readLine().trim());
					String[] candidates = new String[candidateCount];
					for(int j = 0; j < candidateCount; ++j){
						candidates[j] = reader.readLine();
					}
					
					String currentLine = reader.readLine();
					// linked list because we always need to iterate (no random access)
					LinkedList<int[]> votes = new LinkedList<int[]>();
					while(currentLine != null && !currentLine.equals("")){
						String[] currBallStr = currentLine.trim().split(" ", candidateCount);
						int[] ballot = new int[candidateCount];
						for(int j = 0; j < candidateCount; ++j){
							ballot[j] = Integer.parseInt(currBallStr[j]);
						}
						votes.add(ballot);
						currentLine = reader.readLine();
					}
					executeVoting(candidates, votes);
					if(testcaseCount > 1){
						OUTPUT.append(System.lineSeparator());
					}
				}
            }
			System.out.print(OUTPUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static void executeVoting(String[] candidates, LinkedList<int[]> ballots){
		// the number of votes a candidate have to reach in order to be elected
		int majorityThreshold = (int) Math.ceil(ballots.size() / 2.0);
		
		int remainingCandidates = candidates.length;
		int[] candidateVotes = new int[candidates.length];
		int rank1 = determineRank1(candidateVotes, ballots, remainingCandidates);
		
		while(rank1 >= 0 && candidateVotes[rank1] < majorityThreshold){
			remainingCandidates -= eliminateLowestCandidates(candidateVotes, ballots);
			candidateVotes = new int[candidates.length];
			rank1 = determineRank1(candidateVotes, ballots, remainingCandidates);
		}
		
		if(rank1 < 0){
			int voteCount = candidateVotes[-rank1];
			for(int i = 0; i < candidates.length; i++){
				if(candidateVotes[i] == voteCount){
					OUTPUT.append(candidates[i]);
					OUTPUT.append(System.lineSeparator());
				}
			}
		} else {	
			OUTPUT.append(candidates[rank1]);
			OUTPUT.append(System.lineSeparator());
		}
	}
	
	public static int determineRank1(int[] candidateVotes, LinkedList<int[]> ballots, int remainingCandidates){
		int maxCandidate = 0;
		int maxCount = 0;
		ballots.forEach(b -> {
			System.err.println(Arrays.toString(b));
		});
		for(int[] ballot : ballots){
			// get highest ranked ballot for any non-eliminated-candidate
			int i = 0;
			int preferredVote = ballot[i++] - 1;
			while(preferredVote == -2){
				preferredVote = ballot[i++] - 1;
			}
			
			candidateVotes[preferredVote] = candidateVotes[preferredVote] + 1;
			if(candidateVotes[preferredVote] > candidateVotes[maxCandidate]){
				maxCandidate = preferredVote;
				maxCount = 1;
			} else if(candidateVotes[preferredVote] == candidateVotes[maxCandidate]){
				maxCandidate = preferredVote; // maxCandidate should not be 0 when all candidates have the same amount of votes
				maxCount++;
			}
		}

		// if all candidates have same amount of votes, return negative value
		if(maxCount == remainingCandidates){
			return -maxCandidate;
		} else {
			return maxCandidate;
		}
	}
	
	public static int eliminateLowestCandidates(int[] candidateVotes, LinkedList<int[]> ballots){
		int min = Integer.MAX_VALUE;
		LinkedList<Integer> toEliminate = new LinkedList<Integer>();
		for(int i = 0; i < candidateVotes.length; ++i){
			if(candidateVotes[i] == min){
				toEliminate.add(i);
			} else if(candidateVotes[i] < min){
				toEliminate.clear();
				toEliminate.add(i);
				min = candidateVotes[i];
			}
		}
		
		for(int candToEliminate : toEliminate){	
			for(int[] ballot : ballots){
				for(int i = 0; i < ballot.length; ++i){
					if(candToEliminate == ballot[i] - 1){
						ballot[i] = -1;
					}
				}
			}
		}
		
		return toEliminate.size();
	}
}