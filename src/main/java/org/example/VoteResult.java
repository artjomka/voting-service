package org.example;

public sealed interface VoteResult {

    record Accepted(String shareholderId) implements VoteResult {}

    record Changed(String shareholderId) implements VoteResult {}

    record Rejected(String shareholderId, String reason) implements VoteResult {}
}
