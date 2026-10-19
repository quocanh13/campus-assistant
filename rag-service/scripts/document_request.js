import fs from "fs";
import path from "path";

const documentFolderPath = "../dataset";

const adminToken = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6ImUxNTRhOTEwLTU5MWItNDliNC04ODQwLTRiZDEyNDkyZWFlMCIsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3OTAwNjIzOTIsImV4cCI6MTgwMDA2MjM5Mn0.OGYytqdzcqf5wa30Fn2jRSl1uv3GJ3XpksywA_fI4_A"

async function uploadDocument(fileName){
    const filePath = path.join(documentFolderPath, fileName);

    if (!fs.statSync(filePath).isFile()) {
        return;
    }

    const formData = new FormData();
    const fileBuffer = fs.readFileSync(filePath);
    const file = new Blob([fileBuffer], {
        type: "application/pdf"
    });
    formData.append("file", file, fileName);

    const response = await fetch("http://localhost:8080/documents", {
        method: "POST",
        body: formData,
        headers: {
            Authorization: `Bearer ${adminToken}`
        }
    });

    const result = await response.json();
    console.log(`${fileName}\n`, result);
}

async function uploadAllDocuments(){
    const files = fs.readdirSync(documentFolderPath);
    for (const fileName of files) {
        uploadDocument(fileName)
    }
}

async function getDocumentCount(){
    const response = await fetch("http://localhost:8080/documents/count", {
        method: "GET",
        headers: {
            Authorization: `Bearer ${adminToken}`
        }
    });
    const responseData = await response.json()
    return responseData.data
}

async function getAllDocuments() {
    const limit = await getDocumentCount()

    const response = await fetch("http://localhost:8080/documents?index=0&limit=" + limit, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${adminToken}`
        }
    });

    const responseData = await response.json()
    return responseData.data
}

async function uploadAllNewDocuments() {
    const documents = await getAllDocuments()
    const files = fs.readdirSync(documentFolderPath);

    async function check(fileName) {
        for(const document of documents)
            if(document.name == fileName) return;
        uploadDocument(fileName)
    }

    for (const fileName of files)
        check(fileName)
}

async function deleteDocument(id) {
    const response = await fetch("http://localhost:8080/documents/" + id, {
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${adminToken}`
        }
    });
    const responseData = await response.json()
    return responseData;
}

async function deleteAllDocuments() {
    const documents = await getAllDocuments()
    for(const document of documents){
        deleteDocument(document.id).then(responseData => console.log(responseData))
    }
}

const documentRequests = {
    getAllDocuments,
    deleteAllDocuments,
    uploadAllDocuments,
    uploadAllNewDocuments
}

for(let i = 2; i < process.argv.length; i++){
    documentRequests[process.argv[i]]()
}