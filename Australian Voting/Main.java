import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.LinkedList;

/**
 * 10142 - Australian Voting
 * First, all candidates and ballots are read. Ballots are lazily readed, that means 
 * I don't parse the whole ballot string, but only the leftmost "vote" whenever a vote must be counted.
 * In 'initialVoteCount' all highest ranked votes are counted and for each candidate, the voters are memorized.
 * After this initial count, if a winner is determined the election is finished and the winner written to the OUTPUT buffer
 * If not yet a winner is elected, in 'evaluateVoteRound' the first task is to find all eliminated candidates.
 * If  now all candidates are eliminated, this means that the election resulted in a tie. In this case, the 'winners' are those canddiates eliminated
 *     right before this check. Those are written to the OUTPUT buffer
 * Else via the candidateVoters, each ballot that vote for the eliminated candidate(s) is further parsed until a vote is found for a candidate still available.
 * This approach therefore does not need to recount all ballots after the first round and therefore saves much time.
 * 
 *
 * 
 * @date 10.01.2020
 * @author Samuel Keusch
 */
public class Main {
	// collect all output in a StringBuilder (its faster than System.out)
	public static final StringBuilder OUTPUT = new StringBuilder();
	
	// linked list
	public static class ListItem {
		public ListItem next;
		public final int value;
		
		public ListItem(int value, ListItem next){
			this.next = next;
			this.value = value;
		}
	}
	
	/**
	 *
	 */
	public static class Ballot {
		// the remaining ballot string. For efficiency, the ballot is lazily read
		private String remainingBallot;
		
		public Ballot(String ballot){
			this.remainingBallot = ballot;
		}
		
		public int parseNext(){
			int to = 0;
			if(remainingBallot.length() == 1 || remainingBallot.charAt(1) == ' '){
				to = 1;
			} else {
				to = 2;
			}
			
			int vote = Integer.parseInt(remainingBallot.substring(0, to));
			if(remainingBallot.length() > to + 1){
				remainingBallot = remainingBallot.substring(to + 1); // only store remaining votes
				assert remainingBallot.charAt(0) != ' ';
			} else {
				remainingBallot = null;
			}
			return vote - 1; // the topmost vote is returned as int
		}
	}
	
    public static void main(String[] args) {
		//long millis = System.nanoTime();
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
					ArrayList<Ballot> ballots = new ArrayList<>();
					while(currentLine != null && !currentLine.equals("")){
						ballots.add(new Ballot(currentLine.trim())); // ballots are lazily evaluated
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
	public static int s_majorityThreshold; // thresold for winning the election
	
	public static void executeVoting(String[] candidates, List<Ballot> ballots){
		// the number of votes a candidate have to reach in order to be elected
		s_majorityThreshold = (int) Math.ceil(ballots.size() / 2.0);
		s_eliminatedCount = 0;
		
		int[] candidateVotes = new int[candidates.length]; // stores the count of votes for each candidate
		ListItem[] candidateVoters = new ListItem[candidates.length]; // store the ballots that voted for each candidate
		
		int[] winners = initialVoteCount(candidateVotes, candidateVoters, ballots);
		int round = 1;
		while(winners == null){
			//System.err.println("Round " + round++ + " ----------------");
			winners = evaluateVoteRound(candidateVotes, candidateVoters, ballots);
			//System.err.println("---------------------------------");
		}
		

		// print all winners
		for(int i = 0; i < winners.length; i++){
			OUTPUT.append(candidates[winners[i]]);
			OUTPUT.append(System.lineSeparator());
		}
	}

	
	/**
	 * the initial count of all votes
	 * 
	 * @return the amount of winner (1 or 0)
	 */
	public static int[] initialVoteCount(int[] candidateVotes, ListItem[] candidateVoters, List<Ballot> ballots){
		int maxCandidate = 0; // the candidate with the most votes (so far)
		
		// loop through all ballots and count for the highest ranked candidate
		for(int i = 0; i < ballots.size(); ++i){
			int preferredVote = ballots.get(i).parseNext(); // get highest ranked vote
			candidateVotes[preferredVote] = candidateVotes[preferredVote] + 1; // increment count for candidate
			
			candidateVoters[preferredVote] = new ListItem(i, candidateVoters[preferredVote]); // keep reference to ballot
			
			if(candidateVotes[preferredVote] > candidateVotes[maxCandidate]) { // keep track of the candidate with the most votes
				maxCandidate = preferredVote;
			}
		}
		
		// because only 1 candidate can have > 50% the case of a tied election falls into the else.
		// the tie election is the handled in the evaluateVoteRound function
		if(candidateVotes[maxCandidate] > s_majorityThreshold){
			return new int[]{maxCandidate};
		} else {
			return null;
		}
	}
	
	/**
	 *  returns the count of winners (or 0 if not yet a winner)
	 */
	public static int[] evaluateVoteRound(int[] candidateVotes, ListItem[] candidateVoters, List<Ballot> ballots){
		int eliminateCount = 0; // track the amount of eliminated candidates (used for determining the tied candidates in case of a tie)
		
		// find all candidates that need to be eliminated this round
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < candidateVotes.length; ++i){
			if(candidateVotes[i] != -1){ // -1 means the candidate is already eliminated
				if(candidateVotes[i] == min){
					eliminateCount++;
				} else if(candidateVotes[i] < min){
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
		s_eliminatedCount += eliminateCount;
		
		// collect candidates to eliminate
		int count = 0;
		int [] toEliminate = new int[eliminateCount];
		for(int i = 0; i < candidateVotes.length && count < eliminateCount; ++i){ // search winners
			if(candidateVotes[i] == min){
				toEliminate[count++] = i;
			}
		}
		
		if(candidateVotes.length == s_eliminatedCount){
			return toEliminate; // return winners in case of a tie election
		}
		
		
		for(int i = 0; i < toEliminate.length; ++i){ // loop through each candidate which has to be eliminated
			int candidate = toEliminate[i];
			candidateVotes[candidate] = -1; // set the votes for the eliminated candidate to 0
			
			ListItem currentBallotRef = candidateVoters[candidate]; // get the ballot ref (of the first ballot that voted for the eliminated candidate)
			
			/**
			 * Loop through all ballots, that voted for the eliminated candidate.
			 * For each ballot, the next not-yet eliminated candidate is "searched"
			 * after this block, all ballot Are "clean" again, that means the first vote on each ballot is still a valid candidate
			 */
			while(currentBallotRef != null){ // loop through all ballots that voted for this candidate
				candidateVoters[candidate] = currentBallotRef.next; // remove current candidate from candidate voters
				
				Ballot currentBallot = ballots.get(currentBallotRef.value); // get ballot which voted for the eliminated candidate 
				
				int nextPreferredVote;
				do { // find next highest ranked candidate that was not eliminated yet
					nextPreferredVote = currentBallot.parseNext();
				} while(candidateVoters[nextPreferredVote] == null);
				candidateVotes[nextPreferredVote] += 1; // increment vote for the new highest ranked candidate for that ballot
				
				currentBallotRef.next = candidateVoters[nextPreferredVote]; // (1/2)prepend ballot ref to new candidate voters list
				candidateVoters[nextPreferredVote] = currentBallotRef; // (2/2)

				currentBallotRef = candidateVoters[candidate]; // go to next ballot ref
			}
			assert candidateVoters[candidate] == null; // should be null because we iterate until the end
		}
		
		// check if now a candidate has won
		for(int i = 0; i < candidateVotes.length; ++i){
			if(candidateVotes[i] > s_majorityThreshold){
				return new int[]{i};
			}
		}
		
		return null; // no candidate is winner so far
	}
}