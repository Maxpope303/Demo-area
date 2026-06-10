# Slack Notifications Setup Guide

## Overview
Your GitHub Actions pipeline now includes Slack notifications for:
- ✅ **Build Success** - When build and tests pass
- ❌ **Build Failure** - When build or tests fail
- 🚀 **Deployment Approval** - When production deployment is approved

## Setup Instructions

### Step 1: Create a Slack Incoming Webhook

1. **Go to Slack API**
   - Visit: https://api.slack.com/apps
   - Click "Create New App" → "From scratch"

2. **Configure Your App**
   - **App Name**: `GitHub Actions CI/CD`
   - **Workspace**: Select your workspace
   - Click "Create App"

3. **Enable Incoming Webhooks**
   - In the left sidebar, click "Incoming Webhooks"
   - Toggle "Activate Incoming Webhooks" to **ON**
   - Click "Add New Webhook to Workspace"

4. **Select Channel**
   - Choose the channel where notifications should be posted (e.g., `#deployments`, `#ci-cd`, `#dev`)
   - Click "Allow"

5. **Copy Webhook URL**
   - You'll see a webhook URL like:
     ```
     https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX
     ```
   - **Copy this URL** - you'll need it in the next step

### Step 2: Add Webhook to GitHub Secrets

1. **Go to Repository Settings**
   - Navigate to your GitHub repository
   - Click **Settings** → **Secrets and variables** → **Actions**

2. **Create New Secret**
   - Click "New repository secret"
   - **Name**: `SLACK_WEBHOOK_URL`
   - **Value**: Paste the webhook URL from Step 1
   - Click "Add secret"

### Step 3: Test the Integration

1. **Trigger a Build**
   ```bash
   # Make a small change and push
   git commit --allow-empty -m "Test Slack notifications"
   git push origin demo-java8-baseline
   ```

2. **Check Slack**
   - Go to your Slack channel
   - You should see a notification when the build completes

## Notification Examples

### ✅ Build Success Notification
```
✅ Build & Test Passed

Repository: your-org/simple-pharmacy
Branch: demo-java8-baseline
Java Version: 8
Triggered by: username

[View Workflow] [View Commit]
```

### ❌ Build Failure Notification
```
❌ Build & Test Failed

Repository: your-org/simple-pharmacy
Branch: demo-modernized
Java Version: 17
Triggered by: username

⚠️ Action Required: Check the workflow logs for details

[View Logs]
```

### 🚀 Deployment Approval Notification
```
🚀 Production Deployment Approved

Repository: your-org/simple-pharmacy
Branch: modernized
Approved by: username
Java Version: 17

✅ All checks passed:
• Build: Success
• Tests: Passed
• Security: Scanned
• Container: Built
• Coverage: Met threshold

[View Deployment]
```

## Customization Options

### Change Notification Channel

To send notifications to different channels for different events:

1. Create multiple webhooks for different channels
2. Add them as separate secrets:
   - `SLACK_WEBHOOK_BUILD` - for build notifications
   - `SLACK_WEBHOOK_DEPLOY` - for deployment notifications
   - `SLACK_WEBHOOK_SECURITY` - for security alerts

3. Update the workflow to use the appropriate webhook:
   ```yaml
   env:
     SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_BUILD }}
   ```

### Customize Message Format

Edit the `payload` section in `.github/workflows/ci-cd.yml`:

```yaml
- name: Notify Slack - Build Success
  uses: slackapi/slack-github-action@v1.26.0
  with:
    payload: |
      {
        "text": "Your custom message here",
        "blocks": [
          # Add your custom blocks
        ]
      }
```

### Add @mentions

To mention users or channels in notifications:

```yaml
"text": "<!channel> Build failed! Please investigate."
# or
"text": "<@U123456> Your build is ready for review"
```

### Add More Context

You can add additional fields to notifications:

```yaml
{
  "type": "mrkdwn",
  "text": "*Commit Message:*\n${{ github.event.head_commit.message }}"
}
```

## Troubleshooting

### Notifications Not Appearing

1. **Check Secret Name**
   - Ensure the secret is named exactly `SLACK_WEBHOOK_URL`
   - Check for typos or extra spaces

2. **Verify Webhook URL**
   - Test the webhook manually:
     ```bash
     curl -X POST -H 'Content-type: application/json' \
       --data '{"text":"Test message"}' \
       YOUR_WEBHOOK_URL
     ```

3. **Check Workflow Logs**
   - Go to Actions → Select workflow run
   - Check the "Notify Slack" step for errors

4. **Verify Permissions**
   - Ensure the Slack app has permission to post in the channel
   - Check if the webhook is still active in Slack

### Webhook Expired

If you see "invalid_token" errors:
1. Go to Slack API → Your App → Incoming Webhooks
2. Delete the old webhook
3. Create a new webhook
4. Update the GitHub secret with the new URL

### Rate Limiting

Slack has rate limits for webhooks:
- **1 message per second** per webhook
- If you hit limits, consider:
  - Combining multiple notifications
  - Using different webhooks for different events
  - Adding delays between notifications

## Advanced Features

### Conditional Notifications

Send notifications only for specific branches:

```yaml
- name: Notify Slack
  if: github.ref == 'refs/heads/main'
  uses: slackapi/slack-github-action@v1.26.0
```

### Thread Replies

To reply to a previous message (requires storing thread_ts):

```yaml
payload: |
  {
    "thread_ts": "${{ env.THREAD_TS }}",
    "text": "Update: Tests completed"
  }
```

### Rich Formatting

Use Slack's Block Kit for advanced layouts:
- https://api.slack.com/block-kit
- https://app.slack.com/block-kit-builder

## Security Best Practices

1. ✅ **Never commit webhook URLs** to your repository
2. ✅ **Use GitHub Secrets** for all sensitive data
3. ✅ **Rotate webhooks** periodically
4. ✅ **Limit webhook permissions** to specific channels
5. ✅ **Monitor webhook usage** in Slack API dashboard

## Support

- **Slack API Documentation**: https://api.slack.com/messaging/webhooks
- **GitHub Actions Slack Action**: https://github.com/slackapi/slack-github-action
- **Block Kit Builder**: https://app.slack.com/block-kit-builder

---

**Last Updated**: 2026-06-10  
**Maintained By**: DevOps Team

<!-- Made with Bob -->