import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
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
	
	public static class ListItem {
		public final ListItem next;
		public final int value;
		
		public ListItem(int value, ListItem next){
			this.next = next; 
			this.value = value;
		}
	}
	

	
    public static void main(String[] args) {
		long millis = System.nanoTime();
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
					ArrayList<ListItem> ballots = new ArrayList<>();
					while(currentLine != null && !currentLine.equals("")){
						String[] currBallStr = currentLine.trim().split(" ", candidateCount);
						ListItem vote = null;
						for(int j = currBallStr.length - 1; j >= 0; --j){
							vote = new ListItem(Integer.parseInt(currBallStr[j]) - 1, vote);
						}
						ballots.add(vote);
						currentLine = reader.readLine();
					}
					executeVoting(candidates, ballots);
					if(testcaseCount > 1 && i < testcaseCount - 1){
						OUTPUT.append(System.lineSeparator());
					}
				}
            }
			System.out.print(OUTPUT);
			//System.err.println("Took " + ((System.nanoTime() - millis) / 1_000_000) + "ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
			
	public static int s_eliminatedCount ; // the amount of eliminated candidates
	public static ListItem s_eliminatedCandidates; // the candidates currently eliminated
	public static int s_singleWinner; // the index of the winner candidate in case of a single winner
	public static int s_majorityThreshold;
	public static String[] s_candidates;
	
	public static void executeVoting(String[] candidates, List<ListItem> ballots){
		// the number of votes a candidate have to reach in order to be elected
		s_majorityThreshold = (int) Math.ceil(ballots.size() / 2.0);
		s_eliminatedCount = 0;
		s_eliminatedCandidates = null;
		s_singleWinner = -1;
		s_candidates = candidates;
		
		int[] candidateVotes = new int[candidates.length]; // stores the count of votes for each candidate
		ListItem[] candidateVoters = new ListItem[candidates.length]; // store the ballots that voted for each candidate
		int winnerCount = initialVoteCount(candidateVotes, candidateVoters, ballots);
		int round = 1;
		while(winnerCount == 0){
			//System.err.println("Round " + round++ + " ----------------");
			winnerCount = evaluateVoteRound(candidateVotes, candidateVoters, ballots);
			//System.err.println("---------------------------------");
		}
		
		if(winnerCount == 1){
			OUTPUT.append(candidates[s_singleWinner]);
			OUTPUT.append(System.lineSeparator());
		} else {
			printWinners(s_eliminatedCandidates, winnerCount - 1);
		}
	}
	
	public static void printWinners(ListItem winner, int winnerCount){
		if(winnerCount > 0){
			printWinners(winner.next, winnerCount - 1);
		}
		OUTPUT.append(s_candidates[winner.value]);
		OUTPUT.append(System.lineSeparator());
	}
	
	/**
	 * the initial count of all votes
	 * 
	 * @return the amount of winner (1 or 0)
	 */
	public static int initialVoteCount(int[] candidateVotes, ListItem[] candidateVoters, List<ListItem> ballots){
		int maxCandidate = 0; // the candidate with the most votes (so far)
		
		// loop through all ballots and count for the highest ranked candidate
		for(int i = 0; i < ballots.size(); ++i){
			int preferredVote = ballots.get(i).value; // get highest ranked vote
			candidateVotes[preferredVote] = candidateVotes[preferredVote] + 1; // increment count for candidate
			
			candidateVoters[preferredVote] = new ListItem(i, candidateVoters[preferredVote]); // keep reference to ballot
			
			if(candidateVotes[preferredVote] > candidateVotes[maxCandidate]) { // keep track of the candidate with the most votes
				maxCandidate = preferredVote;
			}
		}
		
		// because only 1 candidate can have > 50% the case of a tied election falls into the else.
		// the tie election is the handled in the evaluateVoteRound function
		if(candidateVotes[maxCandidate] > s_majorityThreshold){
			s_singleWinner = maxCandidate;
			return 1;
		} else {
			return 0;
		}
	}

	
	/**
	 *  returns the count of winners (or 0 if not yet a winner)
	 */
	public static int evaluateVoteRound(int[] candidateVotes, ListItem[] candidateVoters, List<ListItem> ballots){
		ListItem toEliminate = null; // linked list of candidates eliminated in this round
		int eliminateCount = 0; // track the amount of eliminated candidates (used for determining the tied candidates in case of a tie)
		
		// find all candidates that need to be eliminated this round
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < candidateVotes.length; ++i){
			if(candidateVotes[i] != -1){ // -1 means the candidate is already eliminated
				if(candidateVotes[i] == min){
					toEliminate = new ListItem(i, toEliminate);
					eliminateCount++;
				} else if(candidateVotes[i] < min){
					toEliminate = new ListItem(i, s_eliminatedCandidates);
					eliminateCount = 1;
					min = candidateVotes[i];
				}
			}
		}
		
		/*
		ListItem x = toEliminate;
		System.err.println("Min vote is " + min + " / Eliminate count: " + eliminateCount + " / Full count " + (s_eliminatedCount + eliminateCount));
		for(int i = 0; i < eliminateCount; i++){
			System.err.println("Eliminate " + s_candidates[x.value] + " | " + (x.value + 1));
			x = x.next;
		}/**/
		
		// merge NEWLY eliminated candidates with already eliminated candidates
		s_eliminatedCandidates = toEliminate;
		s_eliminatedCount += eliminateCount;
		
		// in case all candidates got now eliminated, that means those eliminated last were all tied.
		if(candidateVotes.length == s_eliminatedCount){
			if(eliminateCount == 1){
				s_singleWinner = toEliminate.value;
			}
			return eliminateCount; // amount of candidates tied (candidates are in the s_eliminatedCandidates)
		}
		

		
		
		ListItem currentElCan = s_eliminatedCandidates; // loop through all NEWLY eliminated candidates
		for(int i = 0; i < eliminateCount; i++){
			candidateVotes[currentElCan.value] = -1; // set the votes for the eliminated candidate to 0
			
			ListItem currentBallotRef = candidateVoters[currentElCan.value]; // get the ballot ref (of the first ballot that voted for the eliminated candidate)
			candidateVoters[currentElCan.value] = null; // reset candidateVoters to use as lookup below
			
			/**
			 * Loop through all ballots, that voted for the eliminated candidate.
			 * For each ballot, the next not-yet eliminated candidate is "searched"
			 * after this block, ballots is "clean" again, that means the first vote on each ballot is still a valid candidate
			 */
			while(currentBallotRef != null){ // loop through all ballots that voted for this candidate
				ListItem currentBallot = ballots.get(currentBallotRef.value); // get ballot which voted for the eliminated candidate 
				
				do { // find next highest ranked candidate that was not eliminated yet
					currentBallot = currentBallot.next;
					assert currentBallot != null; 
				} while(candidateVoters[currentBallot.value] == null);
				candidateVotes[currentBallot.value] += 1; // increment vote for the new highest ranked candidate for that ballot
				
				candidateVoters[currentBallot.value] = new ListItem(currentBallotRef.value, candidateVoters[currentBallot.value]);
				ballots.set(currentBallotRef.value, currentBallot); // set the new ballot in the ballots list
				currentBallotRef = currentBallotRef.next; // go to next ballot ref
			}
			currentElCan = currentElCan.next; // go to next eliminated candidate
		}
		
		for(int i = 0; i < candidateVotes.length; ++i){
			if(candidateVotes[i] > s_majorityThreshold){
				s_singleWinner = i;
				return 1;
			}
		}
		
		return 0; // no candidate is winner so far
	}
}