// initMongoDB.js

// Connect to MongoDB
db = connect("mongodb://localhost:27017/tuo_database");

// Default Users
const users = [
    { _id: ObjectId(), userId: "user1_id", userName: "user1_name", userEmail: "user1_email" },
    { _id: ObjectId(), userId: "user2_id", userName: "user2_name", userEmail: "user2_email" },
    { _id: ObjectId(), userId: "user3_id", userName: "user3_name", userEmail: "user3_email" }
];

// Default Teams
const teams = [
    { _id: ObjectId(), teamId: "team1_id", projectId: "project1_id", teamName: "team1_name", creationTimeStamp: Date.now(), teamDescription: "team1_description", users: [users[0]._id] },
    { _id: ObjectId(), teamId: "team2_id", projectId: "project1_id", teamName: "team2_name", creationTimeStamp: Date.now(), teamDescription: "team2_description", users: [users[1]._id] },
    { _id: ObjectId(), teamId: "team3_id", projectId: "project1_id", teamName: "team3_name", creationTimeStamp: Date.now(), teamDescription: "team3_description", users: [users[2]._id] }
];

// Default Project
const project = {
    _id: ObjectId(),
    projectId: "project1_id",
    projectName: "project1_name",
    projectCreationDate: new Date(),
    projectDescription: "project1_description",
    teams: teams.map(team => team._id)
};

// Default Tags
const tags = [
    { tagValue: "example1", colorHex: "#FF5733", createdBy: users[0].userId },
    { tagValue: "example2", colorHex: "#33FF57", createdBy: users[1].userId },
    { tagValue: "example3", colorHex: "#3357FF", createdBy: users[2].userId }
];

// Clean existing collections
db.users.drop();
db.teams.drop();
db.projects.drop();
db.tags.drop();

// Insert default data
db.users.insertMany(users);
db.teams.insertMany(teams);
db.projects.insertOne(project);
db.tags.insertMany(tags);

print("Database populated with default values.");