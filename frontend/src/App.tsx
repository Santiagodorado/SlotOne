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
import BusinessDetail from './pages/BusinessDetail'
import MisReservas from './pages/MisReservas'
import PanelReservas from './pages/PanelReservas'
import PanelTrabajadores from './pages/PanelTrabajadores'

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
        <Route path="/panel/trabajadores" element={<PanelTrabajadores />} />
        <Route path="/panel/horarios" element={<PanelHorarios />} />
        <Route path="/panel/reservas" element={<PanelReservas />} />
        <Route path="/negocio/:id" element={<BusinessDetail />} />
        <Route path="/mis-reservas" element={<MisReservas />} />
        <Route path="/admin" element={<AdminPanel />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
