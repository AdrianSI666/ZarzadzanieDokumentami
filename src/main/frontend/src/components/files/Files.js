import axios from "axios";
import { Button, Modal } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useState, useEffect, useCallback } from "react";
import Dropzone from "../Dropzone";
import { Pagination } from "@material-ui/lab";

const Files = () => {
  const [message, setMessage] = useState("");
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [fileInfos, setFileInfos] = useState([]);
  const [fileUploadModalShow, setFileUploadModalShow] = useState(false);

  function getDocuments(page) {
    axios.get("http://localhost:8091/documents", { params: { page: page } })
      .then(res => {
        setFileInfos(res.data.data)
        setPage(res.data.currentPage)
        setTotalPages(res.data.totalPages)
      }).catch(() => {
        setMessage("Could not get files!")
      });
  }

  useEffect(() => {
    getDocuments(page)
  }, [page]);

  const onDrop = useCallback((acceptedFiles, page) => {
    Promise.all(acceptedFiles.map((file) => {
      const formData = new FormData();
      formData.append("file", file);
      axios.post(
        `http://localhost:8091/files`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data"
          }
        }).then(() => {
          setMessage("Plik pomyślnie dodany do bazy")
          getDocuments(page)
        }).catch(() => {
          setMessage("Wystąpił błąd podczas wysyłania pliku do bazy")
        })
    }))
    setTimeout(function () {
      setMessage(undefined)
    }, 4000);
  }, [])

  function download(e, id, name) {
    e.preventDefault()
    axios({
      url: `http://localhost:8091/files/${id}`,
      method: "GET",
      headers: "anything",
      responseType: "blob"
    }).then(response => {
      if (typeof window.navigator.msSaveBlob !== 'undefined') {
        window.navigator.msSaveBlob(response.data, name);
      }
      else {
        const url = (window.URL && window.URL.createObjectURL) ? window.URL.createObjectURL(response.data) : window.webkitURL.createObjectURL(response.data);
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute(
          "download",
          `${name}`
        );
        link.click();
        URL.revokeObjectURL(url)
      }
    });
  }

  function deleteFile(e, id) {
    e.preventDefault()
    axios.delete(`http://localhost:8091/documents/${id}`).then(() => {
      setMessage("Pomyślnie usunięto plik")
      getDocuments(page)
    }).catch(() => {
      setMessage("Wystąpił błąd podczas usuwania pliku")
    })
    setTimeout(function () {
      setMessage(undefined)
    }, 4000);
  }

  const handlePageChange = (event, value) => {
    setPage(value)
  };

  return (
    <div>

      <Button variant='success' onClick={() => { setFileUploadModalShow(true); }}>
        Dodaj plik
      </Button>

      {message && (<div className="alert alert-light" role="alert">
        {message}
      </div>
      )}

      <div className="card">
        <div className="card-header">List of Files</div>
        <ul className="list-group list-group-flush">
          {fileInfos.length > 0 && fileInfos.map((file, index) => (
            <li className="list-group-item" key={index}>
              <span>{file.title}</span>
              <Button variant='primary' onClick={(e) => { download(e, file.id, file.title); }}>
                Pobierz
              </Button>
              <Button variant="danger" onClick={(e) => { deleteFile(e, file.id); }}>
                Usuń
              </Button>
            </li>
          ))}
        </ul>
      </div>
      <div className="d-flex justify-content-center">
        <Pagination
          className="my-3"
          color="primary"
          showFirstButton 
          showLastButton 
          count={totalPages}
          page={page}
          siblingCount={3}
          boundaryCount={2}
          variant="outlined"
          shape="rounded"
          onChange={handlePageChange}
        />
      </div>
      <Modal
        show={fileUploadModalShow}
        onHide={() => setFileUploadModalShow(false)}
        size="lg"
        aria-labelledby="contained-modal-title-vcenter"
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title id="contained-modal-title-vcenter">
            Dodanie pliku
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Dropzone onDrop={(acceptedFiles) => {
            onDrop(acceptedFiles, page)
            setFileUploadModalShow(false)
          } }/>
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={() => setFileUploadModalShow(false)}>Zamknij</Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default Files;
