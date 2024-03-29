import {
  forwardRef,
  useImperativeHandle,
  useRef,
  FormEvent,
  RefObject,
  useEffect,
  useState,
} from 'react'
import { createPortal } from 'react-dom'
import { Form } from 'react-router-dom'
import axios from 'axios'
import { TextInput, Button, Radio, Label, Select } from 'flowbite-react'
import { API_URL, formDataToRequestData } from '../utils/api'
import { getDepartmentAllList } from '../utils/Address'
import { getPositionCodeList } from '../utils/Code'
import { getAccessToken } from '../utils/auth'

export interface SignupRef {
  open: () => void
}

interface DepartmentInfo {
  departmentName:string
  departmentId: number
}

interface PositonInfo {
  codeId: number
  codeCode: number
  codeName: string
}

const SIGNUP_URL = API_URL + '/admin/user/join'

const Signup = forwardRef<SignupRef>(function Signup(_props, ref) {
  const dialog = useRef<HTMLDialogElement>(null)
  const formRef = useRef<HTMLFormElement>(null);
  const [departmentList, setDepartmentList] = useState<DepartmentInfo[]>([]);
  const [positionList, setPositionList] = useState<PositonInfo[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      const departmentResponse = await getDepartmentAllList() as DepartmentInfo[];
      const positionResponse = await getPositionCodeList() as PositonInfo[]
      setDepartmentList(departmentResponse);
      setPositionList(positionResponse)
    };
    fetchData();
  }, []);

  useImperativeHandle(
    ref,
    () => ({
      open() {
        dialog.current?.showModal()
      },
    }),
    [],
  )
  


  function handleSignUp(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const signupFormData = new FormData(event.currentTarget)
    const signupRequestData = formDataToRequestData(signupFormData)

    axios
      .post(SIGNUP_URL, signupRequestData, {
        headers: {
          Authorization: 'Bearer ' + getAccessToken(),
        },
      })
      .then((response) => {
        alert("회원가입 성공!")
        console.log(response.data)
        formRef.current?.reset()
        dialog.current?.close()
      })
      .catch((error) => {
        console.log(error)
        alert(error.response.data.message)
      })
  }

  return createPortal(
    <dialog
      className="rounded-xl p-10 w-2/3 max-w-xl min-w-fit"
      ref={dialog as RefObject<HTMLDialogElement>}
    >
      <h1 className="text-3xl text-center mb-10">구성원 추가</h1>
      <Form ref={formRef} className="flex flex-col gap-4" onSubmit={handleSignUp}>
        <div>
          <Label className="text-lg">사번</Label>
          <TextInput type="text" name="userEid" required />
        </div>
        <div>
          <Label className="text-lg">이름</Label>
          <TextInput type="text" name="userNameFirst" required />
        </div>
        <div>
          <Label className="text-lg">성</Label>
          <TextInput type="text" name="userNameLast" required />
        </div>
        <div>
          <Label className="text-lg">부서</Label>
          <Select name="userDepartmentId" required >
            {departmentList.map((departmentInfo: DepartmentInfo) => {
              return <option key={departmentInfo.departmentId} value={departmentInfo.departmentId}>{departmentInfo.departmentName}</option>
            })}
          </Select>
        </div>
        <div>
          <Label className="text-lg">직급</Label>
          <Select name="userPositionCodeId" required >
            {positionList.map((positonInfo : PositonInfo ) => {
              return <option key={positonInfo.codeId} value={positonInfo.codeId}>{positonInfo.codeName}</option>
            })}
          </Select>
        </div>
        <div>
          <Label className="text-lg">이메일</Label>
          <TextInput type="email" name="userEmail" required />
        </div>
        <div>
          <Label className="text-lg">전화번호</Label>
          <TextInput type="tel" name="userTel" required/>
        </div>
        <div>
          <Label className="text-lg">주소</Label>
          <TextInput
            type="text"
            name="userAddress"
            placeholder="주소"
            required
          />
        </div>
        <div className="flex gap-4">
          <div className="">
            <Radio id="male" name="userGender" value="M" />
            <Label htmlFor="male">남성</Label>
          </div>
          <div>
            <Radio id="female" name="userGender" value="F" />
            <Label htmlFor="female">여성</Label>
          </div>
        </div>
        <Button className="mt-10" type="submit">
          구성원 추가
        </Button>
      </Form>
    </dialog>,
    document.getElementById('modal')! as HTMLElement,
  )
})

export default Signup
