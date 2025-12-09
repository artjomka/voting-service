package org.example;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class VoteProcessor {

    private static final Map<String, Set<String>> MEETING_PROPOSALS = Map.of(
            "M1", Set.of("P1", "P2", "P3"),
            "M2", Set.of("P4", "P5"),
            "M3", Set.of("P6", "P7")
    );

    /**
     * Processes a vote for a shareholder meeting.
     * If the vote is accepted (new shareholder), the shareholderId is added to existingVoters.
     *
     * @param vote the vote to process
     * @param existingVoters mutable set that will be modified on successful new vote
     * @param recordDate the record date for the meeting
     * @param currentDate the current date for determining if vote changes are allowed
     * @return the result of the vote processing
     * @throws InvalidProposalException if the proposal is not valid for the meeting
     */
    public VoteResult processVote(Vote vote, Set<String> existingVoters,
                                  LocalDate recordDate, LocalDate currentDate) {

        Objects.requireNonNull(vote, "vote must not be null");
        Objects.requireNonNull(existingVoters, "existingVoters must not be null");
        Objects.requireNonNull(recordDate, "recordDate must not be null");
        Objects.requireNonNull(currentDate, "currentDate must not be null");

        validateProposal(vote.meetingId(), vote.proposalId());

        if (!existingVoters.contains(vote.shareholderId())) {
            existingVoters.add(vote.shareholderId());
            return new VoteResult.Accepted(vote.shareholderId());
        }

        if (currentDate.isBefore(recordDate)) {
            return new VoteResult.Changed(vote.shareholderId());
        }

        return new VoteResult.Rejected(vote.shareholderId(), "Vote change not allowed after record date");
    }

    private void validateProposal(String meetingId, String proposalId) {
        var validProposals = MEETING_PROPOSALS.get(meetingId);
        if (validProposals == null || !validProposals.contains(proposalId)) {
            throw new InvalidProposalException(meetingId, proposalId);
        }
    }
}
