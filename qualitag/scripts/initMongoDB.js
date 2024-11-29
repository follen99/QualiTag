// Connect to the MongoDB server
const db = connect("mongodb://localhost:27017/qualitag");

// Collections
const usersCollection = db.getCollection("user");
const tagsCollection = db.getCollection("tag");
const teamsCollection = db.getCollection("team");
const projectsCollection = db.getCollection("project");

// Clear existing data
usersCollection.deleteMany({});
tagsCollection.deleteMany({});
teamsCollection.deleteMany({});
projectsCollection.deleteMany({});

// Insert Users
const users = [
    {
        _id: ObjectId("67497aa9c6d53e225d939d27"),
        username: "user1",
        email: "email1@example.com",
        passwordHash: "$2a$12$w7E3/2bLkNSEmiV2QDCT/OSnHfnuFajr6Wufz302h832QFyQ.lbVi",
        name: "John",
        surname: "Doe",
        projectIds: [],
        teamIds: [],
        tagIds: [],
        _class: "it.unisannio.studenti.qualitag.model.User"
    },
    {
        _id: ObjectId("67497bc0c6d53e225d939d29"),
        username: "user2",
        email: "email2@example.com",
        passwordHash: "$2a$12$u3E4/3cLkOSEjiZ3PFET/OQnHfkuFajr7Xufd313h833RFxR.mdTx",
        name: "Jane",
        surname: "Smith",
        projectIds: [],
        teamIds: [],
        tagIds: [],
        _class: "it.unisannio.studenti.qualitag.model.User"
    },
    {
        _id: ObjectId("67497bc1c6d53e225d939d30"),
        username: "user3",
        email: "email3@example.com",
        passwordHash: "$2a$12$z3E4/4dLkTSEjiY4PFET/OPoHfouFajr8Xufd414h833RFxR.ndTy",
        name: "Alice",
        surname: "Brown",
        projectIds: [],
        teamIds: [],
        tagIds: [],
        _class: "it.unisannio.studenti.qualitag.model.User"
    }
];

usersCollection.insertMany(users);

// Insert Tags
const tags = [
    {
        _id: ObjectId("67497b34c6d53e225d939d28"),
        createdBy: users[0]._id,
        tagValue: "URGENT",
        colorHex: "#FF0000",
        _class: "it.unisannio.studenti.qualitag.model.Tag"
    },
    {
        _id: ObjectId("67497b34c6d53e225d939d29"),
        createdBy: users[1]._id,
        tagValue: "LOW",
        colorHex: "#00FF00",
        _class: "it.unisannio.studenti.qualitag.model.Tag"
    },
    {
        _id: ObjectId("67497b34c6d53e225d939d30"),
        createdBy: users[2]._id,
        tagValue: "BUG",
        colorHex: "#0000FF",
        _class: "it.unisannio.studenti.qualitag.model.Tag"
    }
];

tagsCollection.insertMany(tags);

// Associate tags with users
usersCollection.updateOne({ _id: users[0]._id }, { $push: { tagIds: tags[0]._id } });
usersCollection.updateOne({ _id: users[1]._id }, { $push: { tagIds: tags[1]._id } });
usersCollection.updateOne({ _id: users[2]._id }, { $push: { tagIds: tags[2]._id } });

// Insert Teams
const teams = [
    {
        _id: ObjectId("67497bd8c6d53e225d939d2a"),
        users: [users[0]._id, users[1]._id],
        teamName: "Team Alpha",
        creationTimeStamp: NumberLong("1697040000000"),
        teamDescription: "Team working on a top-priority project.",
        _class: "it.unisannio.studenti.qualitag.model.Team"
    },
    {
        _id: ObjectId("67497bd8c6d53e225d939d2b"),
        users: [users[1]._id, users[2]._id],
        teamName: "Team Beta",
        creationTimeStamp: NumberLong("1697140000000"),
        teamDescription: "Team responsible for testing and debugging.",
        _class: "it.unisannio.studenti.qualitag.model.Team"
    }
];

teamsCollection.insertMany(teams);

// Associate teams with users
usersCollection.updateOne({ _id: users[0]._id }, { $push: { teamIds: teams[0]._id } });
usersCollection.updateOne({ _id: users[1]._id }, { $push: { teamIds: teams[0]._id, teamIds: teams[1]._id } });
usersCollection.updateOne({ _id: users[2]._id }, { $push: { teamIds: teams[1]._id } });

// Insert Projects
const projects = [
    {
        _id: ObjectId("67497c00c6d53e225d939d2b"),
        projectName: "Project X",
        description: "A groundbreaking project for innovation.",
        ownerId: users[0]._id,
        teamId: teams[0]._id,
        tags: [tags[0]._id],
        _class: "it.unisannio.studenti.qualitag.model.Project"
    },
    {
        _id: ObjectId("67497c00c6d53e225d939d2c"),
        projectName: "Project Y",
        description: "A support project for existing applications.",
        ownerId: users[1]._id,
        teamId: teams[1]._id,
        tags: [tags[1]._id, tags[2]._id],
        _class: "it.unisannio.studenti.qualitag.model.Project"
    }
];

projectsCollection.insertMany(projects);

// Associate projects with users and teams
usersCollection.updateOne({ _id: users[0]._id }, { $push: { projectIds: projects[0]._id } });
usersCollection.updateOne({ _id: users[1]._id }, { $push: { projectIds: projects[1]._id } });
teamsCollection.updateOne({ _id: teams[0]._id }, { $push: { projectIds: projects[0]._id } });
teamsCollection.updateOne({ _id: teams[1]._id }, { $push: { projectIds: projects[1]._id } });

print("Database populated with multiple entities successfully!");
