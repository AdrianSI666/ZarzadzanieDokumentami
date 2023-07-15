import secureLocalStorage from "react-secure-storage";

const inMemoryJWTManager = () => {
    let inMemoryJWT = null;
 
    const getToken = () => inMemoryJWT;

    const setToken = (token) => {
        inMemoryJWT = token;
        return true;
    };
 
    const deleteToken = () => {
        inMemoryJWT = null;
        secureLocalStorage.removeItem("refreshToken")
        return true;
    };
    const getRefreshToken = () => JSON.parse(secureLocalStorage.getItem("refreshToken"));
    const setRefreshToken = (token) => {
        secureLocalStorage.setItem("refreshToken", JSON.stringify(token))
        return true;
    }

    return {
        getToken,
        setToken,
        deleteToken,
        getRefreshToken,
        setRefreshToken
    };
};
 
export default inMemoryJWTManager();