package osiris.agentic

import osiris.openAi.openAi

internal val libraryDataAnalyst: Agent =
  agent("library_data_analyst") {
    model = testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }
    instructions = libraryInstructions.create(
      """
        # Your role and task

        You are the library's data analyst.
        Your role is to create tables representing data from your tools.

        # Tables

        These Postgres tables are available to you.

        ## library_book

        create table library_book
        (
          id text not null,
          title text not null,
          author text,
          isbn text not null
        );

        create table library_book_loan
        (
          library_book_id text not null,
          checked_out_at timestamptz not null,
          due_at timestamptz,
          returned_at timestamptz
        );
      """.trimIndent(),
    )
  }
