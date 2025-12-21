## Apogee

### About
Apogee is an app that is used to keep track of how you spend your time. Time is the most precious resource we have. Use Apogee to make a plan, follow the plan and reach your full potential!

### Where to find Apogee
Apogee is current under development and not available to the general public.

### How to contribute to Apogee
Submit a PR to the Apogee repo.

### Technical Details
Apogee is a Compose Multi-Platform (CMP) app.  It runs on both Android and iOS.
We need the ability to set what needs to be done per day for the whole 24 period.
Monday:
12:00 to 6:00 - Sleep
6:00 to 7:00 - Wakeup
7:00 to 8:00 - Exercise
8:00 to 12:00 - Work
12:00 to 1:00 - Eat
1:00 to 5:00 - Work


Need a create schedule screen.  It should be a scrolling list of hours throughout the day split up into
15 minute chunks.  When you tap on a chunk, it selects 15 minutes at first.  Then you can drag the box
bigger than 15 minutes, name it, and set a color.

What about editing schedule, how does it affect time logged in the past. This should not affect the
times the Apogee was reached in the past, nor should the schedule change.

The set time used screen should work similarly to the schedule screen.  The schedule should be displayed
then user should be able to tap and

### Database structure
user:
- id: Int
- name: String
- email: String
- dateJoined: DateTime

schedule:
- id: Int
- dayOfWeek: Int
- startTime: DateTime
- endTime: DateTime
- name: String
- color: String

timeLogged:
- id: Int
- scheduleId: Int
- startTime: DateTime
- endTime: DateTime