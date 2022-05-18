package org.springframework.boot.liquibase;

public abstract class _FactoryProvider {
  public static LiquibaseChangelogMissingFailureAnalyzer liquibaseChangelogMissingFailureAnalyzer(
      ) {
    return new LiquibaseChangelogMissingFailureAnalyzer();
  }

  public static LiquibaseDatabaseInitializerDetector liquibaseDatabaseInitializerDetector() {
    return new LiquibaseDatabaseInitializerDetector();
  }
}
