package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("VoteProcessor")
class VoteProcessorTest {

    private VoteProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new VoteProcessor();
    }

    @Nested
    @DisplayName("when processing new votes")
    class NewVoteTests {

        @Test
        @DisplayName("should accept vote from shareholder who has not voted yet")
        void shouldAcceptNewVote() throws InvalidProposalException {
            var vote = new Vote("SH1", "M1", "P1");
            var existingVoters = new HashSet<String>();
            var recordDate = LocalDate.of(2025, 1, 20);
            var currentDate = LocalDate.of(2025, 1, 15);

            var result = processor.processVote(vote, existingVoters, recordDate, currentDate);

            assertThat(result).isEqualTo(VoteResult.ACCEPTED);
        }

        @Test
        @DisplayName("should add shareholder to existing voters set when vote is accepted")
        void shouldAddShareholderToExistingVotersWhenNewVoteAccepted() throws InvalidProposalException {
            var vote = new Vote("SH1", "M1", "P1");
            var existingVoters = new HashSet<String>();
            var recordDate = LocalDate.of(2025, 1, 20);
            var currentDate = LocalDate.of(2025, 1, 15);

            processor.processVote(vote, existingVoters, recordDate, currentDate);

            assertThat(existingVoters).contains("SH1");
        }

        @Test
        @DisplayName("should accept new vote even on record date")
        void shouldAcceptNewVoteOnRecordDate() throws InvalidProposalException {
            var vote = new Vote("SH1", "M1", "P1");
            var existingVoters = new HashSet<String>();
            var recordDate = LocalDate.of(2025, 1, 20);
            var currentDate = LocalDate.of(2025, 1, 20);

            var result = processor.processVote(vote, existingVoters, recordDate, currentDate);

            assertThat(result).isEqualTo(VoteResult.ACCEPTED);
        }

        @Test
        @DisplayName("should accept new vote even after record date")
        void shouldAcceptNewVoteAfterRecordDate() throws InvalidProposalException {
            var vote = new Vote("SH1", "M1", "P1");
            var existingVoters = new HashSet<String>();
            var recordDate = LocalDate.of(2025, 1, 20);
            var currentDate = LocalDate.of(2025, 1, 25);

            var result = processor.processVote(vote, existingVoters, recordDate, currentDate);

            assertThat(result).isEqualTo(VoteResult.ACCEPTED);
        }
    }

    @Nested
    @DisplayName("when processing vote changes")
    class VoteChangeTests {

        @Test
        @DisplayName("should allow vote change when current date is before record date")
        void shouldAcceptVoteChangeBeforeRecordDate() throws InvalidProposalException {
            var vote = new Vote("SH1", "M1", "P1");
            var existingVoters = new HashSet<>(Set.of("SH1"));
            var recordDate = LocalDate.of(2025, 1, 20);
            var currentDate = LocalDate.of(2025, 1, 15);

            var result = processor.processVote(vote, existingVoters, recordDate, currentDate);

            assertThat(result).isEqualTo(VoteResult.VOTE_CHANGED);
        }

        @ParameterizedTest(name = "should reject vote change when current date is {0} (record date: 2025-01-20)")
        @CsvSource({
                "2025-01-20, on record date",
                "2025-01-25, after record date"
        })
        @DisplayName("should reject vote change on or after record date")
        void shouldRejectVoteChangeOnOrAfterRecordDate(LocalDate currentDate, String scenario)
                throws InvalidProposalException {
            var vote = new Vote("SH1", "M1", "P1");
            var existingVoters = new HashSet<>(Set.of("SH1"));
            var recordDate = LocalDate.of(2025, 1, 20);

            var result = processor.processVote(vote, existingVoters, recordDate, currentDate);

            assertThat(result).isEqualTo(VoteResult.REJECTED_RECORD_DATE_PASSED);
        }
    }

    @Nested
    @DisplayName("when validating proposals")
    class ProposalValidationTests {

        @Test
        @DisplayName("should throw InvalidProposalException for proposal not belonging to meeting")
        void shouldThrowInvalidProposalExceptionForInvalidProposal() {
            var vote = new Vote("SH1", "M1", "INVALID");
            var existingVoters = new HashSet<String>();
            var recordDate = LocalDate.of(2025, 1, 20);
            var currentDate = LocalDate.of(2025, 1, 15);

            assertThatThrownBy(() -> processor.processVote(vote, existingVoters, recordDate, currentDate))
                    .isInstanceOf(InvalidProposalException.class)
                    .hasMessageContaining("INVALID")
                    .hasMessageContaining("M1");
        }

        @Test
        @DisplayName("should throw InvalidProposalException for unknown meeting")
        void shouldThrowInvalidProposalExceptionForUnknownMeeting() {
            var vote = new Vote("SH1", "UNKNOWN", "P1");
            var existingVoters = new HashSet<String>();
            var recordDate = LocalDate.of(2025, 1, 20);
            var currentDate = LocalDate.of(2025, 1, 15);

            assertThatThrownBy(() -> processor.processVote(vote, existingVoters, recordDate, currentDate))
                    .isInstanceOf(InvalidProposalException.class)
                    .hasMessageContaining("UNKNOWN");
        }

        @Test
        @DisplayName("should validate proposal before adding shareholder to voters set")
        void shouldValidateProposalBeforeProcessingVote() {
            var vote = new Vote("SH1", "M1", "INVALID");
            var existingVoters = new HashSet<String>();
            var recordDate = LocalDate.of(2025, 1, 20);
            var currentDate = LocalDate.of(2025, 1, 15);

            assertThatThrownBy(() -> processor.processVote(vote, existingVoters, recordDate, currentDate))
                    .isInstanceOf(InvalidProposalException.class);

            assertThat(existingVoters).isEmpty();
        }
    }
}
