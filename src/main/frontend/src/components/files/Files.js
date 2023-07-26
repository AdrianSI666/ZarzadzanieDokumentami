import axios from "axios";
import { Button, Row, Col, Form, Table, Alert } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import './Files.css';
import React, { useState, useEffect } from "react";
import { Pagination } from "@material-ui/lab";
import Drawer from '@material-ui/core/Drawer';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import inMemoryJWTMenager from "../../services/inMemoryJWTMenager";
import jwtDecode from "jwt-decode";
import { useDispatch, useSelector } from "react-redux";
import { loggOfUser } from "../redux/config";
import { useNavigate } from "react-router-dom";
const Files = () => {
  const config = useSelector((state) => state.config)
  const localHost=config.localHost
  const port=config.port
  const dispatch = useDispatch();
  
  const navigate = useNavigate();
  const [message, setMessage] = useState("");
  const [accessMessage, setAccessMessage] = useState("");
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [pageSize, setPageSize] = useState(5);

  const [fileInfos, setFileInfos] = useState([]);
  const [typesOfDocuments, setTypesOfDocuments] = useState([]);

  const [textFilter, setTextFilter] = useState("");
  const [titleFilter, setTitleFilter] = useState("");
  const [dateFromFilter, setDateFromFilter] = useState("");
  const [dateBeforeFilter, setDateToFilter] = useState("");
  const [costFilterFrom, setCostFilterFrom] = useState("");
  const [costFilterTo, setCostFilterTo] = useState("");
  const [paidFilter, setPaidFilter] = useState(null);
  const [typeFilter, setTypeFilter] = useState(-1);
  const [filterParams, setFilterParams] = useState({});
  const [sort1, setSort1] = useState(-1);
  const [sort1Desc, setSort1Desc] = useState(false);
  const [sort2, setSort2] = useState(-1);
  const [sort2Desc, setSort2Desc] = useState(false);
  const [sort3, setSort3] = useState(-1);
  const [sort3Desc, setSort3Desc] = useState(false);
  const [sort4, setSort4] = useState(-1);
  const [sort4Desc, setSort4Desc] = useState(false);
  const [sort5, setSort5] = useState(-1);
  const [sort5Desc, setSort5Desc] = useState(false);
  const [sortParams, setSortParams] = useState({});

  const [moreThan1Sort, setMoreThan1Sort] = useState(false)
  const [moreThan2Sort, setMoreThan2Sort] = useState(false)
  const [moreThan3Sort, setMoreThan3Sort] = useState(false)
  const [moreThan4Sort, setMoreThan4Sort] = useState(false)
  axios.defaults.headers.common["Authorization"] = `Bearer ${inMemoryJWTMenager.getToken()}`;

  axios.interceptors.request.use( async config => {
    if(inMemoryJWTMenager.getToken() != null && inMemoryJWTMenager.getRefreshToken() != null){
        let decodedToken = jwtDecode(inMemoryJWTMenager.getToken())
        let dateNow = new Date().valueOf()/1000;
        if(decodedToken.exp < dateNow){
          const uninterceptedAxiosInstance = axios.create();
          await uninterceptedAxiosInstance({
            url: `http://${localHost}:${port}/token/refresh`,
            method: "GET",
            headers: {
              Authorization: `Bearer ${inMemoryJWTMenager.getToken()}`,
              Refresh: inMemoryJWTMenager.getRefreshToken()
            }
          }).then(res =>{
            inMemoryJWTMenager.setToken(res.data.access_token)
            config.headers.authorization = `Bearer ${res.data.access_token}`;
            inMemoryJWTMenager.setRefreshToken(res.data.refresh_token)
          }).catch(()=>{
            setAccessMessage("Nie można potwierdzić twojej tożsamości. Zaloguj się ponownie.")
            inMemoryJWTMenager.deleteToken()
            dispatch(loggOfUser())
          }) 
        }
        }else if(inMemoryJWTMenager.getToken() == null && inMemoryJWTMenager.getRefreshToken() != null){
          let decodedToken = jwtDecode(inMemoryJWTMenager.getRefreshToken())
          let dateNow = new Date().valueOf()/1000;
          if(decodedToken.exp > dateNow){
            const uninterceptedAxiosInstance = axios.create();
            await uninterceptedAxiosInstance({
              url: `http://${localHost}:${port}/token/refresh`,
              method: "GET",
              headers: {
                Authorization: `Bearer ${inMemoryJWTMenager.getRefreshToken()}`,
                Refresh: inMemoryJWTMenager.getRefreshToken()
              }
            }).then(res =>{
              inMemoryJWTMenager.setToken(res.data.access_token)
              config.headers.authorization = `Bearer ${res.data.access_token}`;
              inMemoryJWTMenager.setRefreshToken(res.data.refresh_token)
            }).catch(()=>{
              setAccessMessage("Nie można potwierdzić twojej tożsamości. Zaloguj się ponownie.")
              inMemoryJWTMenager.deleteToken()
              dispatch(loggOfUser())
            }) 
        }
    } else if(inMemoryJWTMenager.getToken() == null && inMemoryJWTMenager.getRefreshToken() == null){
      setAccessMessage("Nie można potwierdzić twojej tożsamości. Zaloguj się ponownie.")
      inMemoryJWTMenager.deleteToken()
      dispatch(loggOfUser())
      navigate("/login")
    }
    return config;
    });

  function getDocuments(page, pageSize, sortParams, filterParams) {
    if (Object.keys(filterParams).length !== 0) {
      let params = {
        page,
        "page_size": pageSize,
        ...filterParams,
        ...sortParams
      }
      axios.get(`http://${localHost}:${port}/documents/by`,
        {
          params: params,
          paramsSerializer: {
            indexes: null
          }
        })
        .then(res => {
          setFileInfos(res.data.data)
          setPage(res.data.currentPage)
          setTotalPages(res.data.totalPages)
        }).catch(() => {
          setMessage("Wystąpił błąd podczas filtrowania wyników z bazy danych.")
          setTimeout(function () {
            setMessage(undefined)
          }, 4000);
    })
    } else {
      axios.get(`http://${localHost}:${port}/documents`, {
        params: {
          page: page,
          'page_size': pageSize,
          ...sortParams
        }
      })
        .then(res => {
          setFileInfos(res.data.data)
          setPage(res.data.currentPage)
          setTotalPages(res.data.totalPages)
        }).catch(() => {
          setMessage("Wystąpił błąd podczas pobierania plików, spróbuj ponownie za chwilę.")
          setTimeout(function () {
            setMessage(undefined)
          }, 4000);
        });
    }
  }
  useEffect(() =>{
    axios.get(`http://${localHost}:${port}/types`)
            .then(res => {
              setTypesOfDocuments(res.data)
            }).catch((err) => {
              if(err.response.status != 403) setMessage("Nie wszystkie wymagane dane z bazy danych udało się pobrać. Spróbuj ponownie.")
            })
  }, [])

  useEffect(() => {
      getDocuments(page, pageSize, sortParams, filterParams)
    }, [page]);


  function download(e, id, name) {
    e.preventDefault()
    axios({
      url: `http://${localHost}:${port}/files/${id}`,
      method: "GET",
      headers: {
        "Authorization": `Bearer ${inMemoryJWTMenager.getToken()}`
      },
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
    }).catch(() => {
      setMessage("Błąd podczas pobierania pliku z serwera. Spróbuj ponownie później.")
    });
  }

  function filterResultsBy() {
    let newFilterParams = {};
    if (titleFilter != "") {
      newFilterParams = {
        ...newFilterParams,
        "filter_title": titleFilter
      }
    }
    if (dateFromFilter != "") {
      let range = []
      if (dateBeforeFilter != "") {
        range = [new Date(dateFromFilter), new Date(dateBeforeFilter)]
      } else {
        range = [new Date(dateFromFilter)]
      }
      newFilterParams = {
        ...newFilterParams,
        "filter_date": range
      }
    }
    if (dateFromFilter == "" && dateBeforeFilter != "") {
      newFilterParams = {
        ...newFilterParams,
        "filter_date_before": new Date(dateBeforeFilter)
      }
    }
    if (costFilterFrom != "") {
      const range = [costFilterFrom, costFilterTo]
      newFilterParams = {
        ...newFilterParams,
        "filter_cost": range
      }
    }
    if (costFilterFrom == "" && costFilterTo != "") {
      newFilterParams = {
        ...newFilterParams,
        "filter_cost_to": costFilterTo
      }
    }
    if (paidFilter != undefined) {
      newFilterParams = {
        ...newFilterParams,
        "filter_paid": paidFilter
      }
    }
    if (typeFilter != -1) {
      newFilterParams = {
        ...newFilterParams,
        "filter_type_id": typeFilter
      }
    }
    if (textFilter != "") {
      newFilterParams = {
        ...newFilterParams,
        "text": textFilter
      }
    }
    setFilterParams(newFilterParams);
    getDocuments(1, pageSize, sortParams, newFilterParams)
  }

  function resetFilter() {
    setTitleFilter("");
    setDateFromFilter("");
    setDateToFilter("");
    setCostFilterFrom("");
    setCostFilterTo("");
    setPaidFilter(null);
    setTypeFilter(-1);
    let nullFilter = {}
    if(textFilter !== ""){
      nullFilter = {
        "text": textFilter
    }}
    setFilterParams(nullFilter)
    getDocuments(1, pageSize, sortParams, nullFilter);
  }

  const getSortValueFromId = (id) => {
    if (id == 0) return "sort_title"
    if (id == 1) return "sort_date"
    if (id == 2) return "sort_cost"
    if (id == 3) return "sort_paid"
    if (id == 4) return "sort_type"
  }

  function sortResultsBy() {
    let sortParams = {}
    if (sort1 != "-1") {
      let sortName = getSortValueFromId(sort1)
      sortParams[sortName] = sort1Desc
    }
    if (sort2 != "-1") {
      let sortName = getSortValueFromId(sort2)
      sortParams[sortName] = sort2Desc
    }
    if (sort3 != "-1") {
      let sortName = getSortValueFromId(sort3)
      sortParams[sortName] = sort3Desc
    }
    if (sort4 != "-1") {
      let sortName = getSortValueFromId(sort4)
      sortParams[sortName] = sort4Desc
    }
    if (sort5 != "-1") {
      let sortName = getSortValueFromId(sort5)
      sortParams[sortName] = sort5Desc
    }
    setSortParams(sortParams);
    getDocuments(1, pageSize, sortParams, filterParams)
  }

  function resetSort() {
    setSortParams({})
    getDocuments(1, pageSize, {}, filterParams)
    setSort1(-1)
    setSort2(-1)
    setSort3(-1)
    setSort4(-1)
    setSort5(-1)
  }

  const handlePageChange = (event, value) => {
    setPage(value)
  };

  return (
    <div>
      {accessMessage && (<Alert key={"danger"} variant={"danger"}>
        {accessMessage}
      </Alert>
      )}
      {message && (<Alert key={"warning"} variant={"warning"}>
        {message}
      </Alert>
      )}
      <div className="card">
        <Row>
          <Col>
            <input
              type='text'
              className='input w-100 h-90 m-1'
              value={textFilter}
              onChange={e => setTextFilter(e.target.value)}
              placeholder='Wyszukaj po tekście w dokumeńcie'
            />
          </Col>
          <Col md="auto">
            <Button variant='primary' className='me-1' onClick={() => {
              if (textFilter == "") {
                setMessage("Brak tekstu do szukania, wpisz tekst.")
                setTimeout(function () {
                  setMessage(undefined)
                }, 4000);
              } else {
                let currentFilter = {
                  ...filterParams,
                  "text": textFilter
                }
                setFilterParams(currentFilter)
                getDocuments(1, pageSize, sortParams, currentFilter)
              }
            }}>
              Szukaj
            </Button>
            <Button variant='primary' className='me-1' onClick={() => {
              setTextFilter("")
              delete filterParams.text
              setFilterParams(filterParams)
              getDocuments(1, pageSize, sortParams, filterParams)
            }}>
              Reset wyszukiwania po tekście
            </Button>
            <Button variant='primary' className='me-1' onClick={() => {
              setTextFilter("")
              resetFilter()
              resetSort()
              getDocuments(1, pageSize, {}, {})
            }}>
              Reset wyszukiwania i sortowania
            </Button>
          </Col>
        </Row>
        <div className="card-header">
          Lista dokumentów
        </div>
        <div className="card-header">
          <span>Ilość wyświetlanych dokumentów na stronę:</span>
          <Form.Select className="ms-2 w-6 d-inline" onChange={e => {
            let newPage = Math.ceil((page * pageSize - pageSize + 1) / e.target.value)
            getDocuments(newPage, e.target.value, sortParams, filterParams)
            setPageSize(e.target.value)
          }} defaultValue={5}>
            <option value="5" key={5}>5</option>
            <option value="10" key={10}>10</option>
            <option value="20" key={20}>20</option>
            <option value="40" key={40}>40</option>
            <option value="80" key={80}>80</option>
          </Form.Select>

        </div>
        <Table bordered hover>
          <thead>
            <tr>
              <th className="th-ls">Nazwa dokumentu</th>
              <th className="th-ms">Data do zapłaty</th>
              <th className="th-ss">Do zapłaty</th>
              <th className="th-ss">Opłacony</th>
              <th className="th-ms">Typ dokumentu</th>
              <th colSpan={1}>Akcje</th>
            </tr>
          </thead>
          <tbody>
            {fileInfos.length > 0 && fileInfos.map((file, index) => (
              <tr key={index}>
                <td>{file.title}</td>
                <td>{
                  file.date != null ? new Date(file.date).toLocaleDateString() : "Brak daty"}</td>
                <td>{file.cost != null ? file.cost : "Brak kosztu"}</td>
                <td>{file.cost != null ? file.paid ? "Opłacone" : "Nie opłacone" : ""}</td>
                <td>{file.typeName}</td>
                <td>
                  <Button variant='primary' onClick={(e) => { download(e, file.id, file.title); }}>
                    Pobierz
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
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
      <Drawer
        sx={{
          width: 224,
          '& .MuiDrawer-paper': {
            width: 224,
            boxSizing: 'border-box',
          },
        }}
        variant="permanent"
        anchor="right"
      >
        <h3>Filtry</h3>
        <Divider />
        <List>
          <Form.Group className="mb-3" controlId="formTitleFilter">
            <Form.Label>Tytuł</Form.Label>
            <Form.Control value={titleFilter} type="text" onChange={e => setTitleFilter(e.target.value)} />
          </Form.Group>
          <Form.Group className="mb-3" controlId="formDateFilter">
            <Form.Label>Data od</Form.Label>
            <Form.Control value={dateFromFilter} type="date" onChange={e => { setDateFromFilter(e.target.value) }} />
          </Form.Group>
          <Form.Group className="mb-3" controlId="formDateFilter">
            <Form.Label>Data do</Form.Label>
            <Form.Control value={dateBeforeFilter} type="date" onChange={e => { setDateToFilter(e.target.value) }} />
          </Form.Group>
          <Form.Group className="mb-3" controlId="formCostFilter">
            <Form.Label>Koszt od</Form.Label>
            <Form.Control value={costFilterFrom} type="number" onChange={e => setCostFilterFrom(e.target.value)} />
          </Form.Group>
          <Form.Group className="mb-3" controlId="formCostFilter">
            <Form.Label>Koszt do</Form.Label>
            <Form.Control value={costFilterTo} type="number" onChange={e => setCostFilterTo(e.target.value)} />
          </Form.Group>
          <Form.Group className="mb-3" controlId="formPaidFilter">
            <Form.Check
              type={'radio'}
              id={`paidFilterTrue`}
              name="paidFilter"
              label={`Opłacony`}
              onChange={() => setPaidFilter(true)}
              checked={paidFilter === true}
            />
            <Form.Check
              type={'radio'}
              id={`paidFilterFalse`}
              name="paidFilter"
              label={`Nie opłacony`}
              onChange={() => setPaidFilter(false)}
              checked={paidFilter === false}
            />
            <Button onClick={() => setPaidFilter(null)}>
              Odznacz filter opłacenia
            </Button>
          </Form.Group>
          <Form.Group className="mb-3" controlId="formTitleFilter">
            <Form.Label>Typ</Form.Label>
            <Form.Select value={typeFilter} onChange={e => setTypeFilter(e.target.value)}>
              <option value="-1">Wybierz typ</option>
              {typesOfDocuments.map((types) => {
                return <option value={types.id} key={types.id}>{types.name}</option>
              })}
            </Form.Select>
          </Form.Group>
          <Button className="d-inline" variant="success" onClick={() => { filterResultsBy() }}>Filtruj</Button>
          <Button className="ms-2 d-inline" onClick={() => { resetFilter() }}>Reset filtrów</Button>
          <Divider />
          <h3>Sortowanie</h3>
          <div className="mb-3">
            <span>Sortuj po:</span>
            <Form.Select value={sort1} onChange={e => setSort1(e.target.value)}>
              <option value="-1">Brak</option>
              <option value="0">Nazwa dokumentu</option>
              <option value="1">Data do zapłaty</option>
              <option value="2">Koszt do zapłaty</option>
              <option value="3">Opłacony</option>
              <option value="4">Typ dokumentu</option>
            </Form.Select>
            <Form.Group className="mb-3" controlId="sort1asc">
              <Form.Check
                type={'checkbox'}
                id={`sort1Desc`}
                label={`Odwróć sortowanie`}
                defaultChecked={sort1Desc}
                onChange={e => setSort1Desc(e.target.checked)}
              />
            </Form.Group>
            {!moreThan1Sort && (
              <Button onClick={() => { setMoreThan1Sort(true) }}>
                Dodaj drugi parametr
              </Button>)}
            {moreThan1Sort && (<Button onClick={() => {
              setMoreThan1Sort(false)
              setMoreThan2Sort(false)
              setMoreThan3Sort(false)
              setMoreThan4Sort(false)
            }}>
              Usuń drugi parametr
            </Button>)}
          </div>
          {moreThan1Sort && (
            <div className="mb-3">
              <span>Sortuj po:</span>
              <Form.Select value={sort2} onChange={e => setSort2(e.target.value)}>
                {sort1 != "-1" && (<option value="-1">Brak</option>)}
                {sort1 != "0" && (<option value="0">Nazwa dokumentu</option>)}
                {sort1 != "1" && (<option value="1">Data do zapłaty</option>)}
                {sort1 != "2" && (<option value="2">Koszt do zapłaty</option>)}
                {sort1 != "3" && (<option value="3">Opłacony</option>)}
                {sort1 != "4" && (<option value="4">Typ dokumentu</option>)}
              </Form.Select>
              <Form.Group className="mb-3" controlId="sort2asc">
                <Form.Check
                  type={'checkbox'}
                  id={`sort2Desc`}
                  label={`Odwróć sortowanie`}
                  defaultChecked={sort2Desc}
                  onChange={e => setSort2Desc(e.target.checked)}
                />
              </Form.Group>
              {!moreThan2Sort && (
                <Button onClick={() => { setMoreThan2Sort(true) }}>
                  Dodaj trzeci parametr
                </Button>)}
              {moreThan2Sort && (<Button onClick={() => {
                setMoreThan2Sort(false)
                setMoreThan3Sort(false)
                setMoreThan4Sort(false)
              }}>
                Usuń trzeci parametr
              </Button>)}
            </div>
          )}
          {moreThan2Sort && (
            <div className="mb-3">
              <span>Sortuj po:</span>
              <Form.Select value={sort3} onChange={e => setSort3(e.target.value)}>
                {(sort1 != "-1" && sort2 != "-1") && (<option value="-1">Brak</option>)}
                {(sort1 != "0" && sort2 != "0") && (<option value="0">Nazwa dokumentu</option>)}
                {(sort1 != "1" && sort2 != "1") && (<option value="1">Data do zapłaty</option>)}
                {(sort1 != "2" && sort2 != "2") && (<option value="2">Koszt do zapłaty</option>)}
                {(sort1 != "3" && sort2 != "3") && (<option value="3">Opłacony</option>)}
                {(sort1 != "4" && sort2 != "4") && (<option value="4">Typ dokumentu</option>)}
              </Form.Select>
              <Form.Group className="mb-3" controlId="sort3asc">
                <Form.Check
                  type={'checkbox'}
                  id={`sort3Desc`}
                  label={`Odwróć sortowanie`}
                  defaultChecked={sort3Desc}
                  onChange={e => setSort3Desc(e.target.checked)}
                />
              </Form.Group>
              {!moreThan3Sort && (
                <Button onClick={() => { setMoreThan3Sort(true) }}>
                  Dodaj czwarty parametr
                </Button>)}
              {moreThan3Sort && (<Button onClick={() => {
                setMoreThan3Sort(false)
                setMoreThan4Sort(false)
              }}>
                Usuń czwarty parametr
              </Button>)}
            </div>
          )}
          {moreThan3Sort && (
            <div className="mb-3">
              <span>Sortuj po:</span>
              <Form.Select value={sort4} onChange={e => setSort4(e.target.value)}>
                {(sort1 != "-1" && sort2 != "-1" && sort3 != "-1") && (<option value="-1">Brak</option>)}
                {(sort1 != "0" && sort2 != "0" && sort3 != "0") && (<option value="0">Nazwa dokumentu</option>)}
                {(sort1 != "1" && sort2 != "1" && sort3 != "1") && (<option value="1">Data do zapłaty</option>)}
                {(sort1 != "2" && sort2 != "2" && sort3 != "2") && (<option value="2">Koszt do zapłaty</option>)}
                {(sort1 != "3" && sort2 != "3" && sort3 != "3") && (<option value="3">Opłacony</option>)}
                {(sort1 != "4" && sort2 != "4" && sort3 != "4") && (<option value="4">Typ dokumentu</option>)}
              </Form.Select>
              <Form.Group className="mb-3" controlId="sort3asc">
                <Form.Check
                  type={'checkbox'}
                  id={`sort3Desc`}
                  label={`Odwróć sortowanie`}
                  defaultChecked={sort4Desc}
                  onChange={e => setSort4Desc(e.target.checked)}
                />
              </Form.Group>
              {!moreThan4Sort && (
                <Button onClick={() => { setMoreThan4Sort(true) }}>
                  Dodaj piąty parametr
                </Button>)}
              {moreThan4Sort && (<Button onClick={() => { setMoreThan4Sort(false) }}>
                Usuń piąty parametr
              </Button>)}
            </div>
          )}
          {moreThan4Sort && (
            <div className="mb-3">
              <span>Sortuj po:</span>
              <Form.Select value={sort5} onChange={e => setSort5(e.target.value)}>
                {(sort1 != "-1" && sort2 != "-1" && sort3 != "-1" && sort4 != "-1") && (<option value="-1">Brak</option>)}
                {(sort1 != "0" && sort2 != "0" && sort3 != "0" && sort4 != "0") && (<option value="0">Nazwa dokumentu</option>)}
                {(sort1 != "1" && sort2 != "1" && sort3 != "1" && sort4 != "1") && (<option value="1">Data do zapłaty</option>)}
                {(sort1 != "2" && sort2 != "2" && sort3 != "2" && sort4 != "2") && (<option value="2">Koszt do zapłaty</option>)}
                {(sort1 != "3" && sort2 != "3" && sort3 != "3" && sort4 != "3") && (<option value="3">Opłacony</option>)}
                {(sort1 != "4" && sort2 != "4" && sort3 != "4" && sort4 != "4") && (<option value="4">Typ dokumentu</option>)}
              </Form.Select>
              <Form.Group className="mb-3" controlId="sort3asc">
                <Form.Check
                  type={'checkbox'}
                  id={`sort3Desc`}
                  label={`Odwróć sortowanie`}
                  defaultChecked={sort5Desc}
                  onChange={e => setSort5Desc(e.target.checked)}
                />
              </Form.Group>
            </div>
          )}
          <Button className="d-inline" variant="success" onClick={() => sortResultsBy()}>Sortuj</Button>
          <Button className="mt-2 d-inline" onClick={() => resetSort()}>Reset sortowania</Button>
        </List>
      </Drawer>

    </div>
  );
};

export default Files;
