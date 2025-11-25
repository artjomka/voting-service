# Voting Service

A Java service for processing shareholder votes in meetings.

## Requirements

- Java 25+
- Gradle 9.x

## Business Rules

1. **New votes** are always accepted, regardless of the current date
2. **Vote changes** are only allowed before the record date
3. **Vote changes on or after the record date** are rejected
4. **Invalid proposals** (not belonging to the meeting) throw an exception

## Project Structure

```
src/main/java/org/example/
├── Vote.java                    # Vote record (shareholderId, meetingId, proposalId)
├── VoteResult.java              # Enum: ACCEPTED, VOTE_CHANGED, REJECTED_RECORD_DATE_PASSED
├── VoteProcessor.java           # Core business logic
├── InvalidProposalException.java # Thrown for invalid proposal/meeting combinations
└── Main.java                    # Demo application

src/test/java/org/example/
└── VoteProcessorTest.java       # Unit tests (JUnit 6)
```

## Usage

```java
var processor = new VoteProcessor();
var existingVoters = new HashSet<String>();
var recordDate = LocalDate.of(2025, 1, 20);
var currentDate = LocalDate.now();

var vote = new Vote("SH001", "M1", "P1");
var result = processor.processVote(vote, existingVoters, recordDate, currentDate);
```

## Running

```bash
# Run tests
./gradlew test

# Run demo
./gradlew run
```

## API

### VoteProcessor.processVote()

```java
VoteResult processVote(Vote vote, Set<String> existingVoters,
                       LocalDate recordDate, LocalDate currentDate)
    throws InvalidProposalException
```

| Parameter | Description |
|-----------|-------------|
| vote | The vote to process |
| existingVoters | Mutable set of shareholders who have voted (modified on new vote) |
| recordDate | The meeting's record date |
| currentDate | Current date for determining if changes are allowed |

| Return Value | Condition |
|--------------|-----------|
| `ACCEPTED` | New shareholder vote |
| `VOTE_CHANGED` | Existing shareholder, before record date |
| `REJECTED_RECORD_DATE_PASSED` | Existing shareholder, on or after record date |
| throws `InvalidProposalException` | Invalid proposal for meeting |
