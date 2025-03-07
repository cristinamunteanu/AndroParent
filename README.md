# AndroParent

An Android application for managing children's activities, treatments, and caregivers using Parse as a backend service.

## Features

- User authentication (login, signup, password reset)
- Child management
  - Add/edit child profiles with photos
  - Track child's basic information
  - Manage activities and treatments
  - Assign caregivers

- Activity tracking
  - Schedule and manage activities
  - Set start/end times
  - Assign responsible persons
  - View activities by date ranges

- Treatment management
  - Schedule medications and treatments
  - Set frequencies (hours/days/weeks/months)
  - Track treatment details
  - Assign responsible caregivers

- Caregiver management
  - Add/manage caregivers
  - Set access permissions (read/write)
  - View caregiver details

- Messaging system
  - Send messages between users
  - View sent/received messages
  - Message threading

## Technical Details

- Built for Android
- Uses Parse SDK v1.2.1 for backend services
- Implements user authentication and ACL security
- Calendar integration for scheduling
- Image handling for child photos
- SQLite data persistence
- Custom UI components (DashboardLayout, TextListAdapter)

## Installation

1. Clone the repository
2. Import project into Android Studio/Eclipse
3. Configure Parse credentials
4. Build and deploy to device

## Dependencies

- Parse Android SDK 1.2.1
- Android SDK 
- SQLite database

## Directory Structure

- `src/` - Source code
- `res/` - Android resources (layouts, menus, values)
- `libs/` - External libraries
- `assets/` - Application assets

## License

[License Information]

## Contributing

[Contribution Guidelines]
