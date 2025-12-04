#!/usr/bin/env zsh

# Script to load environment variables from .env file
# Usage: source load-env.zsh

if [ ! -f .env ]; then
    echo "Error: .env file not found"
    return 1
fi

# Export variables from .env file
# Filter out comments and empty lines, handle various .env formats
while IFS= read -r line || [ -n "$line" ]; do
    # Skip comments and empty lines
    [[ "$line" =~ '^#' ]] && continue
    [[ -z "$line" ]] && continue
    
    # Handle both KEY=value and KEY: value formats
    if [[ "$line" =~ '^([A-Za-z_][A-Za-z0-9_]*)[[:space:]]*[:=][[:space:]]*(.*)$' ]]; then
        key="${match[1]}"
        value="${match[2]}"
        
        # Remove quotes if present
        value="${value%\"}"
        value="${value#\"}"
        value="${value%\'}"
        value="${value#\'}"
        
        # Remove any trailing whitespace
        value="${value%%[[:space:]]}"
        
        # Export the variable
        export "$key=$value"
        echo "Exported: $key"
    fi
done < .env

echo "Environment variables loaded successfully!"

