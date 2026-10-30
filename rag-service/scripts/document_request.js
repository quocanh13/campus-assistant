import fs from "fs";
import path from "path";
import metatdata from "../../dataset/metadata.json" with { type: "json" };

const documentFolderPath = "../dataset/documents";

const adminToken = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6ImUxNTRhOTEwLTU5MWItNDliNC04ODQwLTRiZDEyNDkyZWFlMCIsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3OTAwNjIzOTIsImV4cCI6MTgwMDA2MjM5Mn0.OGYytqdzcqf5wa30Fn2jRSl1uv3GJ3XpksywA_fI4_A"

async function uploadDocument(fileInfo){
    const filePath = path.join(documentFolderPath, fileInfo.name);

    const formData = new FormData();
    const fileBuffer = fs.readFileSync(filePath);
    const file = new Blob([fileBuffer], {
        type: "application/pdf"
    });
    formData.append("file", file, fileInfo.name);
    formData.append("from_cohort", String(fileInfo.from_cohort))
    formData.append("to_cohort", String(fileInfo.to_cohort))
    formData.append("valid_at", String(fileInfo.valid_at))
    for (const keyword of fileInfo.keywords) {
        formData.append("keywords", keyword);
    }

    const response = await fetch("http://localhost:8080/documents", {
        method: "POST",
        body: formData,
        headers: {
            Authorization: `Bearer ${adminToken}`
        }
    });

    const result = await response.json();
    console.log(result)
}

async function uploadAllDocuments(){
    for (const fileInfo of metatdata)
        uploadDocument(fileInfo)
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
    if(limit == 0) return []
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
    
    function check(fileInfo) {
        for(const document of documents){
            if(fileInfo.name == document.name)
                return false
        }
        return true
    }

    for (const fileInfo of metatdata)
        if(check(fileInfo))
            uploadDocument(fileInfo)
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