# ADR 0001: Minimal Changes in Code Modifications

## Status
Accepted

## Context
During the development process, there have been instances where changes were made to files beyond what was specifically requested in the prompt. This led to unintended modifications and potential issues.

## Decision
We will strictly follow the principle of minimal changes:
1. Only modify what is explicitly requested in the prompt
2. Do not make additional changes to other parts of the code
3. If a change requires modifications in other files, explicitly ask for permission
4. Document any assumptions made during the implementation

## Consequences
### Positive
- Reduced risk of introducing unintended bugs
- Better control over code changes
- Clearer understanding of what was modified and why
- Easier code review process

### Negative
- May require additional steps to implement related changes
- Could lead to more frequent but smaller commits
- May need to explicitly request permission for related changes

## Examples
### Good
```yaml
# Original request: "Add ActiveMQ configuration"
spring:
  activemq:
    broker-url: tcp://localhost:61616
```

### Bad
```yaml
# Original request: "Add ActiveMQ configuration"
spring:
  activemq:
    broker-url: tcp://localhost:61616
  # Unrequested changes
  datasource:
    url: jdbc:postgresql://localhost:5432/db
    username: postgres
```

## Related Decisions
- None

## References
- None 