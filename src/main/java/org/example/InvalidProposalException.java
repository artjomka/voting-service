package org.example;

public class InvalidProposalException extends RuntimeException {

    public InvalidProposalException(String meetingId, String proposalId) {
        super("Proposal '%s' is not valid for meeting '%s'".formatted(proposalId, meetingId));
    }
}
