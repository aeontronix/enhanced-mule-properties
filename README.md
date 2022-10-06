# Enhanced Mule Properties Provider

This is a mule property provider designed to replace both the standard properties provider and the 
secure properties placeholder.

It provides various significant features lacking from the standard property manager:

- Built-in support for environment-specific variables
- Encryption support that doesn't break studio metadata
- Support file as properties (so for example you can inject keystores as dependencies)
- Support default values in property placeholders like in spring
- Azure Vault integration

# Usage


See [Documentation Website](https://docs.enhanced-mule.com/properties/index.html) for more details
