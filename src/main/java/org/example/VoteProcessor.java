package org.example;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public class VoteProcessor {

    private static final Map<String, Set<String>> MEETING_PROPOSALS = Map.of(
            "M1", Set.of("P1", "P2", "P3"),
            "M2", Set.of("P4", "P5"),
            "M3", Set.of("P6", "P7")
    );

    public VoteResult processVote(Vote vote, Set<String> existingVoters,
                                  LocalDate recordDate, LocalDate currentDate)
            throws InvalidProposalException {

        validateProposal(vote.meetingId(), vote.proposalId());

        if (!existingVoters.contains(vote.shareholderId())) {
            existingVoters.add(vote.shareholderId());
            return VoteResult.ACCEPTED;
        }

        if (currentDate.isBefore(recordDate)) {
            return VoteResult.VOTE_CHANGED;
        }

        return VoteResult.REJECTED_AFTER_RECORD_DATE;
    }

    private void validateProposal(String meetingId, String proposalId) throws InvalidProposalException {
        var validProposals = MEETING_PROPOSALS.get(meetingId);
        if (validProposals == null || !validProposals.contains(proposalId)) {
            throw new InvalidProposalException(meetingId, proposalId);
        }
    }
}
