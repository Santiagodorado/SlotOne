import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import BusinessPanel from './pages/BusinessPanel'
import CreateBusiness from './pages/CreateBusiness'
import EditBusiness from './pages/EditBusiness'
import AdminPanel from './pages/AdminPanel'
import PanelServicios from './pages/PanelServicios'
import PanelHorarios from './pages/PanelHorarios'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/registro" element={<Register />} />
        <Route path="/panel" element={<BusinessPanel />} />
        <Route path="/panel/crear" element={<CreateBusiness />} />
        <Route path="/panel/editar" element={<EditBusiness />} />
        <Route path="/panel/servicios" element={<PanelServicios />} />
        <Route path="/panel/horarios" element={<PanelHorarios />} />
        <Route path="/admin" element={<AdminPanel />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
