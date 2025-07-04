-- Migration script to update appointment status ENUM
-- This adds the missing 'Confirmed' status to the appointments table

-- Show current status column definition
DESCRIBE appointments;

-- Show current status values in the table
SELECT DISTINCT status FROM appointments;

-- Update the status column to include all required values
ALTER TABLE appointments 
MODIFY COLUMN status ENUM('Scheduled', 'Confirmed', 'In Progress', 'Completed', 'Cancelled', 'No Show') 
DEFAULT 'Scheduled';

-- Verify the change
DESCRIBE appointments;

-- Show the updated status column definition
SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_DEFAULT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'appointments' AND COLUMN_NAME = 'status';
