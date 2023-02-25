import axios from "axios";

export default axios.create({
    baseURL: "http://localhost:8091",
    headers: {
        "Content-type": "application/json"
    }
});
