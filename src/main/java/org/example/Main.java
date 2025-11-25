package org.example;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Main {

    private static final LocalDate RECORD_DATE = LocalDate.of(2025, 1, 20);

    private final VoteProcessor processor = new VoteProcessor();
    private final Set<String> existingVoters = new HashSet<>();

    void main() {
        System.out.println("=== Shareholder Vote Processing Demo ===");
        System.out.println("Record Date: " + RECORD_DATE);
        System.out.println();

        newShareholderVotesBeforeRecordDate();
        existingShareholderChangesVoteBeforeRecordDate();
        existingShareholderChangesVoteOnRecordDate();
        newShareholderVotesAfterRecordDate();
        shareholderVotesForInvalidProposal();

        System.out.println("Final voters set: " + existingVoters);
    }

    private void newShareholderVotesBeforeRecordDate() {
        var vote = new Vote("SH001", "M1", "P1");
        var currentDate = LocalDate.of(2025, 1, 15);
        processAndPrint("New shareholder votes before record date", vote, currentDate);
    }

    private void existingShareholderChangesVoteBeforeRecordDate() {
        var vote = new Vote("SH001", "M1", "P2");
        var currentDate = LocalDate.of(2025, 1, 18);
        processAndPrint("Existing shareholder changes vote before record date", vote, currentDate);
    }

    private void existingShareholderChangesVoteOnRecordDate() {
        var vote = new Vote("SH001", "M1", "P3");
        var currentDate = LocalDate.of(2025, 1, 20);
        processAndPrint("Existing shareholder changes vote on record date", vote, currentDate);
    }

    private void newShareholderVotesAfterRecordDate() {
        var vote = new Vote("SH002", "M1", "P1");
        var currentDate = LocalDate.of(2025, 1, 25);
        processAndPrint("New shareholder votes after record date", vote, currentDate);
    }

    private void shareholderVotesForInvalidProposal() {
        var vote = new Vote("SH003", "M1", "INVALID");
        var currentDate = LocalDate.of(2025, 1, 15);
        processAndPrint("Shareholder votes for invalid proposal", vote, currentDate);
    }

    private void processAndPrint(String scenario, Vote vote, LocalDate currentDate) {
        System.out.println("--- " + scenario + " ---");
        System.out.println("Vote: " + vote);
        System.out.println("Current Date: " + currentDate);

        try {
            var result = processor.processVote(vote, existingVoters, RECORD_DATE, currentDate);
            System.out.println("Result: " + result);
        } catch (InvalidProposalException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }
}
